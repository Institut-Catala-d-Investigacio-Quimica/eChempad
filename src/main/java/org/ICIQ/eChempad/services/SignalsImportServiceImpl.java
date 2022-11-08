package org.ICIQ.eChempad.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ICIQ.eChempad.configurations.converters.DocumentWrapperConverter;
import org.ICIQ.eChempad.configurations.wrappers.UserDetailsImpl;
import org.ICIQ.eChempad.entities.DocumentWrapper;
import org.ICIQ.eChempad.entities.genericJPAEntities.*;
import org.ICIQ.eChempad.services.genericJPAServices.DocumentService;
import org.ICIQ.eChempad.services.genericJPAServices.ExperimentService;
import org.ICIQ.eChempad.services.genericJPAServices.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

// https://stackoverflow.com/questions/38705890/what-is-the-difference-between-objectnode-and-jsonnode-in-jackson
@Service
@ConfigurationProperties(prefix = "signals")
public class SignalsImportServiceImpl implements SignalsImportService {

    /**
     * The base URL of the Signals API.
     */
    static final String baseURL = "https://iciq.signalsnotebook.perkinelmercloud.eu/api/rest/v1.0";

    /**
     * If true determines that it should ignore all the resources from which the current user is not an owner. If false
     * import all accessible resources.
     */
    static final boolean getOnlyOwnedResources = false;

    @Autowired
    private JournalService<Journal, UUID> journalService;

    @Autowired
    private ExperimentService<Experiment, UUID> experimentService;

    @Autowired
    private DocumentService<Document, UUID> documentService;
    
    @Autowired
    private WebClient webClient;

    @Autowired
    private DocumentWrapperConverter documentWrapperConverter;

    public String importWorkspace(String APIKey) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        this.getJournals(APIKey, stringBuilder);
        return stringBuilder.toString();
    }

    public String importWorkspace() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        String APIKey = ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getResearcher().getSignalsAPIKey();

        this.getJournals(APIKey, stringBuilder);
        return stringBuilder.toString();
    }

    @Override
    public String importJournal(String APIKey, Serializable id) {
        return null;
    }

    @Override
    public String importJournal(Serializable id) {
        return null;
    }

    static void printJSON(ObjectNode objectNode)
    {
        final ObjectMapper objectMapper = new ObjectMapper();

        try {
            System.out.println("JOURNAL JSON" + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectNode));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


    public void getJournals(String APIKey, StringBuilder stringBuilder)
    {
        ObjectNode journalJSON;
        int i = 0;
        while ((journalJSON = this.getJournal(APIKey, i)) != null)
        {
            // Iterate until the data of the entity is empty
            if (journalJSON.get("data").isEmpty())
            {
                break;
            }
            else
            {
                // Check if the journal owner email coincides with the email of the logged user, if not discard journal
                JsonNode includedData = journalJSON.get("included");
                JsonNode includedOwner = includedData.get(0);
                JsonNode ownerAttributes = includedOwner.get("attributes");
                JsonNode ownerName = ownerAttributes.get("userName");
                String ownerNameTrimmed = ownerName.toString().replace("\"", "");

                if (getOnlyOwnedResources && ! ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getResearcher().getUsername().equals(ownerNameTrimmed))
                {
                    Logger.getGlobal().info("Owner name does not coincide with currently logged user in eChempad application side" + ownerName);

                    Logger.getGlobal().info("attributes " + ownerAttributes.toString());

                    i++;
                    continue;
                }

                // Remove quotes and obtain the EID of this entity.
                String journal_eid = journalJSON.get("data").get(0).get("id").toString().replace("\"", "");

                // Create unmanaged journal to save the metadata
                Journal signalsJournal = new Journal();

                // Parse and log journal name
                String signalsJournalName = journalJSON.get("data").get(0).get("attributes").get("description").toString().replace("\"", "");
                if (signalsJournalName.equals(""))
                {
                    signalsJournalName = "(No name provided)";
                }
                signalsJournal.setName(signalsJournalName);
                stringBuilder.append(" * Journal ").append(i).append(" with EID ").append(journal_eid).append(": ").append(signalsJournalName).append("\n");

                // Parse journal description
                String signalsJournalDescription = journalJSON.get("data").get(0).get("attributes").get("name").toString().replace("\"", "");
                signalsJournal.setDescription(signalsJournalDescription);

                // metadata parsing (...)

                this.journalService.save(signalsJournal);

                // Now call getExperimentsFromJournal using the created journal in order to import their children, recursively
                // This function will fill the passed journal with the new retrieved experiments from Signals. It will also
                // call the function to getDocumentFromExperiment passing the reference of the experiment, to fill the DB.
                this.getExperimentsFromJournal(APIKey, journal_eid, (UUID) signalsJournal.getId(), stringBuilder);
                i++;
                Logger.getGlobal().info("JOURNAL NUMBER " + i + " FINISHED IMPORT");
            }
        }
    }

    public ObjectNode getJournal(String APIKey, int pageOffset)
    {
        return this.webClient.get()
                .uri(SignalsImportServiceImpl.baseURL + "/entities?page[offset]=" + ((Integer) pageOffset).toString() + "&page[limit]=1&includeTypes=journal&include=owner&includeOptions=mine")
                .header("x-api-key", APIKey)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
    }

    public void getExperimentsFromJournal(String APIKey, String journal_eid, UUID journal_uuid, StringBuilder stringBuilder)
    {
        // ArrayNode experiments = this.objectMapper.createArrayNode();
        ObjectNode experimentJSON;
        int i = 0;
        while ((experimentJSON = this.getExperimentFromJournal(APIKey, i, journal_eid)) != null)
        {
            // Iterate until the data of the entity is empty
            if (experimentJSON.get("data").isEmpty())
            {
                break;
            }
            else
            {
                // Here we will call getDocuments, we will append each document into a list inside of
                // journal{data}[0]{relationships}{children} = []
                String experiment_eid = experimentJSON.get("data").get(0).get("id").toString().replace("\"", "");
                System.out.println("EXPERIMENT EID IS: " + experiment_eid);

                // Create unmanaged journal to save the metadata
                Experiment signalsExperiment = new Experiment();

                // Parse and log experiment name
                String signalsExperimentName = experimentJSON.get("data").get(0).get("attributes").get("description").toString().replace("\"", "");
                if (signalsExperimentName.equals(""))
                {
                    signalsExperimentName = "(No name provided)";
                }
                signalsExperiment.setName(signalsExperimentName);
                stringBuilder.append("   - Experiment ").append(i).append(" with EID ").append(experiment_eid).append(": ").append(signalsExperimentName).append("\n");

                // Parse experiment description
                signalsExperiment.setDescription(experimentJSON.get("data").get(0).get("attributes").get("name").toString().replace("\"", ""));

                // metadata parsing (...)

                this.experimentService.addExperimentToJournal(signalsExperiment, journal_uuid);

                // printJSON(experimentJSON);

                // Now call getExperimentsFromJournal using the created journal in order to import their children, recursively
                // This function will fill the passed journal with the new retrieved experiments from Signals. It will also
                // call the function to getDocumentFromExperiment passing the reference of the experiment, to fill the DB.
                this.getDocumentsFromExperiment(APIKey, experiment_eid, (UUID) signalsExperiment.getId(), stringBuilder);

                i++;
            }
        }
    }

    public ObjectNode getExperimentFromJournal(String APIKey, int pageOffset, String journal_eid)
    {
        return this.webClient.get()
                .uri(SignalsImportServiceImpl.baseURL + "/entities/" + journal_eid + "/children?page[offset]=" + ((Integer) pageOffset).toString() + "&page[limit]=1&include=children%2C%20owner")
                .header("x-api-key", APIKey)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
    }

    public void getDocumentsFromExperiment(String APIKey, String experiment_eid, UUID experiment_uuid, StringBuilder stringBuilder)
    {
        ObjectNode documentJSON;
        int i = 0;
        while ((documentJSON = this.getDocumentFromExperiment(APIKey, i, experiment_eid)) != null)
        {
            // Iterate until the data of the entity is empty
            if (documentJSON.get("data").isEmpty())
            {
                break;
            }
            else
            {
                // Parse Signals document
                String document_eid = documentJSON.get("data").get(0).get("id").toString().replace("\"", "");
                DocumentWrapper documentHelper = new DocumentWrapper();

                // Parse document name
                String documentHelperName = documentJSON.get("data").get(0).get("attributes").get("description").toString().replace("\"", "");
                if (documentHelperName.equals(""))
                {
                    documentHelperName = "(No name provided)";
                }
                documentHelper.setName(documentHelperName);

                // Parse document description
                String documentHelperDescription = documentJSON.get("data").get(0).get("attributes").get("name").toString().replace("\"", "");
                if (documentHelperDescription.equals(""))
                {
                    documentHelperDescription = "(No description provided)";
                }
                documentHelper.setDescription(documentHelperDescription);

                // Parse file
                // First we obtain the inputStream of this document, which actually corresponds to a file. We also need
                // to obtain the values of the HTTP header "Content-Disposition" which looks like this in an arbitrary
                // document export:
                //
                // attachment; filename="MZ7-085-DC_10%5B1%5D.zip"; filename*=utf-8''MZ7-085-DC_10%5B1%5D.zip
                //
                // We would need to obtain the data of the filename which is the filed with the name "filename" and in
                // the previous example has the value "MZ7-085-DC_10%5B1%5D.zip"
                // We also need to obtain the header Content-type which indicates the mimetype of the file we are
                // retrieving. That will allow to know the type of file inorder to process it further.
                HttpHeaders receivedHeaders = null;
                try {
                    receivedHeaders = new HttpHeaders();
                    InputStream is = this.exportDocument(APIKey, document_eid, receivedHeaders).getInputStream();
                    Logger.getGlobal().warning("HEADERS: " + receivedHeaders.toString());
                    MultipartFile multipartFile = new MockMultipartFile(document_eid, receivedHeaders.getContentDisposition().getFilename(), receivedHeaders.getContentType().toString(), is);

                    documentHelper.setFile(multipartFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                // File length is not needed to be parsed since it is conserved inside of the bytestream

                // Debug printing
                stringBuilder.append("     # Document ").append(i).append(" with EID ").append(document_eid).append(": ").append(documentHelperName).append("\n");

                // Transform into a DB document
                Logger.getGlobal().warning("Document parsed" + documentHelper.toString());
                Document document = this.documentWrapperConverter.convertToDatabaseColumn(documentHelper);

                // Add the parsed document to its corresponding experiment
                this.documentService.addDocumentToExperiment(document, experiment_uuid);
                i++;
            }
        }
    }

    public ObjectNode getDocumentFromExperiment(String APIKey, int pageOffset, String experiment_eid)
    {
        return this.webClient.get()
                .uri(SignalsImportServiceImpl.baseURL + "/entities/" + experiment_eid + "/children?page[offset]=" + ((Integer) pageOffset) + "&page[limit]=1&include=children%2C%20owner")
                .header("x-api-key", APIKey)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
    }

    public ByteArrayResource exportDocument(String APIKey, String document_eid, HttpHeaders receivedHeaders) throws IOException {

        String url = SignalsImportServiceImpl.baseURL + "/entities/" + document_eid + "/export";

        ResponseEntity<ByteArrayResource> responseEntity = this.webClient.get()
                .uri(url)
                .header("x-api-key", APIKey)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .toEntity(ByteArrayResource.class)
                .block();

        // We are using the headers parameter of the function as an output parameter. But remember that in Java
        // everything is a pointer but args in functions are read-only. So we can modify the inside of the object but
        // not make the pointer go to another location by pointing to another object.
        for (String headerKey: responseEntity.getHeaders().keySet())
        {
            receivedHeaders.put(headerKey, Objects.requireNonNull(responseEntity.getHeaders().get(headerKey)));
        }

        // In the cases where there is stored an empty file in Signals we receive a nullPointer instead of a ByteArrayResource empty
        if (responseEntity.getBody() == null)
        {
            return new ByteArrayResource("".getBytes());
        }
        else
        {
            return responseEntity.getBody();
        }
    }
}
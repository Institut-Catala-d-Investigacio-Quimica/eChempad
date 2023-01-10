## Setup react app

To create a React app using [this](https://www.baeldung.com/spring-boot-react-crud) guide.

To create a React app issue the command with `npm` installed. You can install it by issuing the command:
```bash
sudo apt install -y npm
```

To initialize the `frontend` source folder, issue the following in the PATH where you want to initialize this folder. In 
our case we will do it inside the folder `src/main/frontend`

```bash
npx create-react-app frontend
```

Then `cd` to the `frontend` folder:

```bash
cd frontend
```

Issue the following command to install Bootstrap, React router and reactstrap in the folder of dependencies (this folder
is git ignored).
```bash
npm install --save bootstrap@5.1 react-cookie@4.1.1 react-router-dom@5.3.0 reactstrap@8.10.0
```

Also add the line `import 'bootstrap/dist/css/bootstrap.min.css';` into `app/src/index.js`.

Then from the `frontend` directory issue the command to start the React application server.
```bash 
npm start
```


## Recreate React app
With the source code already initialized in a folder or a repository, we may need to recreate the environment in order 
to run the application. This is basically download all the dependencies needed again in another computer (dependencies
are not uploaded to the repository).



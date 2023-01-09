To create a react app using [this](https://www.baeldung.com/spring-boot-react-crud) guide.

To create a React app issue the command with `npm` installed.

```bash
npx create-react-app frontend
```

Then `cd` to the `frontend` folder and issue the following command to install Bootstrap, React router and reactstrap.
```bash
npm install --save bootstrap@5.1 react-cookie@4.1.1 react-router-dom@5.3.0 reactstrap@8.10.0
```

Also add the line `import 'bootstrap/dist/css/bootstrap.min.css';` into `app/src/index.js`.

Then from the `frontend` directory isue the command:
```bash 
npm start
```
## Setup react app

To create a React app using [this](https://www.baeldung.com/spring-boot-react-crud) guide.

To create a React app issue the command with `npm` installed. You can install it by downloading the binaries from the
download page:
```bash
sudo apt install -y npm
```

To initialize the `frontend` source folder, issue the following in the PATH where you want to initialize this folder. In 
our case we will do it inside the folder `src/mnpm startain/frontend`

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
npm install --save bootstrap@latest react-cookie@latest react-router-dom@latest reactstrap@latest
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



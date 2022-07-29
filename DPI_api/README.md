# Passi rilascio Smart DPI API

## 0) Build version

Scegliere versione della build (es. `1.0`)

`<BUILD_VERSION> = 1.0`

## 1) **API version**
incrementare versione su property `api.version` in `src/main/resources/application.properties` impostando il valore scelto per `<BUILD_VERSION>`

esempio:

`api.version=1.0`

## 2) **Build**
Lanciare script `build_smartdpi_api.sh` per compilazione jar spring boot, passando come parametro `<BUILD_VERSION>` e `<ENV_NAME>` (`dev|coll|prod`). In caso di `env=dev` controllare puntamenti a istanza docker/locale

`./build_smartdpi_api.sh -v <BUILD_VERSION> -e <ENV_NAME> -d`


*esempio 1 (in questo caso viene creato solamente il jar):*

`./build_smartdpi_api.sh -v 1.0 -e dev`


*esempio 2 (in questo caso viene creato il jar e viene creata l'immagine docker con il tag specificato in <BUILD_VERSION>. Puoi saltare al punto 4):*

`./build_smartdpi_api.sh -v 1.0 -e dev -d`

## 3) **Build Docker image** (*opzionale*)
Eseguire tag immagine Docker da `Dockerfile` ( se non si è specificato '-d' su punto 2)

`docker build -t tpn/smartdpi-api:<BUILD_VERSION> --build-arg ENV_NAME=<ENV_NAME> <DOCKERFILE_PATH>`

*esempio:*

`docker build -t tpn/smartdpi-api:1.0 --build-arg ENV_NAME=coll .`

## 4) **Run API**
Lanciare container Docker da immagine appena creata

*! verificare che non ci siano già immagini smartdpi-api in esecuzione/stop !*

`docker run --restart=always -d -p <PORTA_MAPPATA>:8080 --name smartdpi-api tpn/smartdpi-api:<BUILD_VERSION>`

*esempio:*

`docker run --restart=always -d -p 3000:8080 --name smartdpi-api tpn/smartdpi-api:1.0`

---

## !!! Build per DEPLOY !!!
- Scegliere env opportuno.
- Lanciare i comandi per la build del punto **2**
- Eseguire tag della versione appena creata per puntamenti a registry privato.
*esempio:*
`docker tag tpn/smartdpi-api:1.0 tpnregistry.top-network.it:5000/tpn/smartdpi-api:1.0`
- Effettuare push su docker registry. *esempio:* 
`docker push tpnregistry.top-network.it:5000/tpn/smartdpi-api:1.0`
- Eseguire pull e run dell'immagine appena creata dalla macchina opportuna. Per ulteriori dettagli leggere il file readme presente sulla macchina stessa.
- Creare tag SVN del progetto trunk al path `/projects/DPI/DPI_api`. Il tag deve essere creato nella cartella al path `/projects_tags/SmartDPI/DPI_api` sotto il nome di `DPI_api_<BUILD_VERSION>`, es. `DPI_api_1.0`
	

**N.B.** per effettuare la push su registry privato è necessario effettuare la login con il comando `docker login tpnregistry.top-network.it:5000`

# sudo bash build_tnstock_api.sh -v "1.0" -e "prod"
# nella cartella del progetto
# modificare nome del jar in smartdpi.jar
# avere installato la jdk come immagine

############################################################
# Help                                                     #
############################################################
Help()
{
   # Display Help
   echo "Build SmartDpi API script."
   echo
   echo "Syntax: build_smartdpi_api.sh -v"
   echo "options:"
   echo "h     Helper."
   echo "v     Artifact e Docker image version [eg. 1.0]."
   echo "e     Environment name [dev | coll | prod]"
   echo "d     "
   echo
}

############################################################
# Build Maven                                              #
############################################################
BuildMaven()
{
	# print start message
	echo "Building smartdpi API [version ${BUILD_VERSION} - ${ENV_NAME}] ..."
	# build jar
	echo "Building jar [mvn clean package -Dartifact.version=${BUILD_VERSION} -Dspring.profiles.active=${ENV_NAME}]"
	mvn clean package -Dartifact.version=${BUILD_VERSION} -Dspring.profiles.active=${ENV_NAME}
	echo "target/smartdpi-${BUILD_VERSION}.jar built"
	# rename file
	echo "Rename target/smartdpi-${BUILD_VERSION}.jar target/smartdpi.jar"
	mv target/tnstockapi-${BUILD_VERSION}.jar target/tnstockapi.jar
	echo "target/smartdpi.jar built"
	# print end message
	echo "smartdpi API [version ${BUILD_VERSION} - ${ENV_NAME}] build finished"
}

############################################################
# Build Docker                                             #
############################################################
BuildDocker()
{
	# print start message
	echo "Building Docker image [tpn/smartdpi-api:${BUILD_VERSION} - ${ENV_NAME}] ..."
	# build docker image
	docker build -t tpn/smartdpi-api:${BUILD_VERSION} --build-arg ENV_NAME=${ENV_NAME} .
	# print end message
	echo "Docker image [tpn/smartdpi-api:${BUILD_VERSION}] build finished"
}

############################################################
############################################################
# Main program                                             #
############################################################
############################################################

# Set variables
BUILD_VERSION=""
BUILD_DOCKER=true
ENV_NAME=""

############################################################
# Process the input options. Add options as needed.        #
############################################################
# Get the options
while getopts "hdv:e:" option; do
	case $option in
    	h) # display Help
        	Help
         	exit;;
    	v) # build version
         	BUILD_VERSION=$OPTARG;;
      	d) # Build  docker image
      		BUILD_DOCKER=true;;
      	e) # environment
      		ENV_NAME=$OPTARG;;
     \?) # Invalid option
         echo "Error: Invalid option"
         exit;;
   esac
done

############################################################
# Main                                                     #
############################################################

# if version specified
if [[ $BUILD_VERSION != "" ]] && [[ $ENV_NAME != "" ]]
then
	# build jar
	BuildMaven
	# if docker build specified
	if [[ $BUILD_DOCKER = true ]]
	then
		# build docker image
		BuildDocker
	fi
else
	echo "Missing 'v' and 'e' argument."
	echo "See 'build_smartdpi_api --help'"
fi

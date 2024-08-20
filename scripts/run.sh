USER="portfolio-app"
ARTIFACT="${ARTIFACT_FINAL_NAME}.war" # This can be set by maven during a build or provided as an env prop

YELLOW="\e[0;93m"
MAGENTA="\e[0;95m"
CYAN="\e[0;96m"
WHITE="\e[0;97m"
BOLD_GREEN="\e[1;92m"
BOLD_WHITE="\e[1;97m"
RESET="\e[0m"

printf "\n\n${BOLD_WHITE}Starting Portfolio App${RESET}\n\n"

printf "Killing any process already running on ${WHITE}:8080${RESET}...\n"
fuser -k 8080/tcp

printf "Fetching ${WHITE}api.video API key${RESET}...\n"
json=`aws secretsmanager get-secret-value --secret-id apiVideo --query SecretString --output text`
export API_VIDEO_KEY=`echo $json | jq -r ".apiKey"`

printf "Fetching ${WHITE}database credentials${RESET}...\n"
json=`aws secretsmanager get-secret-value --secret-id portfolio-app-rds-access --query SecretString --output text`
host=`echo $json | jq -r ".host"`
port=`echo $json | jq -r ".port"`
db=`echo $json | jq -r ".dbInstanceIdentifier"`
export SPRING_DATASOURCE_URL="jdbc:mysql://${host}:${port}/${db}"
export SPRING_DATASOURCE_USERNAME=`echo $json | jq -r ".username"`
export SPRING_DATASOURCE_PASSWORD=`echo $json | jq -r ".password"`

printf "Starting ${YELLOW}${ARTIFACT}${RESET}...\n"
nohup java -jar ${ARTIFACT} > logs/output.log 2>&1 &

printf "\n${BOLD_GREEN}Portfolio app has started.${RESET}\n"
printf "It may take up to ${WHITE}30 seconds for Spring${RESET} to serve requests\n\n\n"
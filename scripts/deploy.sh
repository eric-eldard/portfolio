ROOT=/opt/portfolio
USER=portfolio-app

GREEN="\e[1;92m"
YELLOW="\e[33m"
MAGENTA="\e[35m"
CYAN="\e[36m"
WHITE="\e[1;97m"
RESET="\e[0m"

printf "\n${WHITE}Deploying Portfolio App${RESET}\n\n"

if [[ ! -d $ROOT ]]
then
  printf "Creating application directory ${CYAN}${ROOT}/${RESET}...\n"
  sudo mkdir $ROOT
fi

printf "Moving ${YELLOW}${ARTIFACT_FINAL_NAME}.war${RESET} to ${CYAN}${ROOT}/${RESET}...\n"
sudo mv ~/${ARTIFACT_FINAL_NAME}.war $ROOT

printf "Moving ${YELLOW}run.sh${RESET} to ${CYAN}${ROOT}/${RESET}...\n"
sudo mv ~/run.sh $ROOT

printf "Setting ${YELLOW}run.sh${RESET} as executable...\n"
sudo chmod u+x $ROOT/run.sh

if [[ ! -d $ROOT/logs ]]
then
  printf "Creating directory ${CYAN}${ROOT}/logs/${RESET}...\n"
  sudo mkdir $ROOT/logs
fi

if ! id -u "$USER" >/dev/null 2>&1
then
  printf "Creating user ${MAGENTA}${USER}${RESET}...\n"
  sudo useradd $USER
  printf "\n"
fi

printf "Changing ownership of ${CYAN}${ROOT}${RESET} to user ${MAGENTA}${USER}${RESET}...\n"
sudo chown -R $USER $ROOT

printf "\n${GREEN}Portfolio app successfully deployed.${RESET}\n\n"
printf "To run, execute ${YELLOW}run.sh${RESET} from ${CYAN}${ROOT}${RESET} as user ${MAGENTA}${USER}${RESET}\n\n"
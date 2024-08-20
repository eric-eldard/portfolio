ROOT="/opt/portfolio"
USER="portfolio-app"
FOLDER="assets"
ARTIFACT="assets.zip"

YELLOW="\e[0;93m"
MAGENTA="\e[0;95m"
CYAN="\e[0;96m"
WHITE="\e[0;97m"
BOLD_GREEN="\e[1;92m"
BOLD_WHITE="\e[1;97m"
RESET="\e[0m"

printf "\n\n${BOLD_WHITE}Deploying Assets${RESET}\n\n"

if [[ ! -d $ROOT ]]
then
  printf "Creating application directory ${CYAN}${ROOT}/${RESET}...\n"
  sudo mkdir $ROOT
fi

if [[ -d $ROOT/$FOLDER ]]
then
  printf "Removing old assets directory...\n"
  sudo rm -rf $ROOT/$FOLDER
fi

printf "Unzipping ${YELLOW}${ARTIFACT}${RESET} to ${CYAN}${ROOT}/${RESET}...\n"
sudo unzip $ARTIFACT -d $ROOT

printf "Removing ${YELLOW}${ARTIFACT}${RESET}...\n"
sudo rm $ARTIFACT
sudo rm -rf $ROOT/__MACOSX

if ! id -u "$USER" >/dev/null 2>&1
then
  printf "Creating user ${MAGENTA}${USER}${RESET}...\n"
  sudo useradd $USER
  printf "\n"
fi

printf "Changing ownership of ${CYAN}${ROOT}/${FOLDER}${RESET} to user ${MAGENTA}${USER}${RESET}...\n"
sudo chown -R $USER $ROOT/$FOLDER

printf "\n${BOLD_GREEN}Portfolio assets successfully deployed.${RESET}\n\n\n"
#!/bin/sh

########################### VARIABLE ###########################################
BASEDIR=$(realpath .)

normal=`echo "\033[0m"`
underscoreFont=`echo "\033[4m"`
boldFont=`echo "\033[1m"`
fgGreen=`echo "\033[32m"`
fgRed=`echo "\033[31m"`

USERS_LIST="michael"
ADM_LIST="karsten sascha vlad"
ALL_USERS="karsten michael sascha vlad"

USERS_PASSWORD="GeBeEr2015"
NETCUP_DEV_ROOT_PASSWORD="xN3sqKm4yQ3awfp"
NETCUP_STAGE_ROOT_PASSWORD=""
NETCUP_WEB_ROOT_PASSWORD="rN4TW1b8uksqQda"

SERVER_LIST="netcup_dev netcup_stage netcup_web"
NETCUP_DEV_IP="85.235.64.62"
NETCUP_STAGE_IP="85.235.64.39"
NETCUP_WEB_IP="94.16.115.192"

################################################################################

########################### FUNCTION ###########################################

function subMenu() {
clear
HOST_IP="$1"
ROOT_PASSWORD="$2"
HOST_NAME="$3"
USER_NAME="$4"

if [[ -z "${USER_NAME}" ]]; then
    read -e -p "Enter your user name > " USER_NAME

	case ${USER_NAME} in
			karsten|michael|sascha|vlad)
			echo "The user name: \"${USER_NAME}\" is accepted"
		 ;;
		*) 	echo "Invalid name ${USER_NAME}"
			echo "Available names are : ${ALL_USERS}"
		   	read -e -p "Enter your user name > " USER_NAME
		  ;;
	esac
fi
echo ""
echo ""

if [[ -n "${USER_NAME}" ]]; then
	echo "1. Add user"
	echo "2. Update the .bash_profile"
	echo "3. Create private/public keys pair"
	echo "4. Copy public key to the server"
	echo "5. Copy install scripts to the server"
	echo "6. Copy spring-boot scripts to the server"
	echo "7. Copy profile script to the server"
	echo "8. Copy sudoers file to the server"

	printf "\n"
	printf "Exit - any key\n"

	read -en 1 -p "> "
	case ${REPLY-} in
		1) addUser "${HOST_IP}" "${ROOT_PASSWORD}" "${USER_NAME}"
			read -p "Click any key..." NULL
			subMenu "${HOST_IP}" "${ROOT_PASSWORD}" "${HOST_NAME}" "${USER_NAME}"
		 ;;
		2) updateProfile "${HOST_IP}" "${HOST_NAME}" "${USER_NAME}"
			read -p "Click any key..." NULL
			subMenu "${HOST_IP}" "${ROOT_PASSWORD}" "${HOST_NAME}" "${USER_NAME}"
		 ;;
		3) createKeys
			read -p "Click any key..." NULL
			subMenu "${HOST_IP}" "${ROOT_PASSWORD}" "${HOST_NAME}" "${USER_NAME}"
		 ;;
		4) copyKey "${HOST_IP}" "${USER_NAME}"
			read -p "Click any key..." NULL
			subMenu "${HOST_IP}" "${ROOT_PASSWORD}" "${HOST_NAME}" "${USER_NAME}"
		 ;;
		5) copyInstallScripts "${HOST_IP}" "${ROOT_PASSWORD}"
			read -p "Click any key..." NULL
			subMenu "${HOST_IP}" "${ROOT_PASSWORD}" "${HOST_NAME}" "${USER_NAME}"
		 ;;
		6) copyScriptBootScripts "${HOST_IP}" "${ROOT_PASSWORD}"
			read -p "Click any key..." NULL
			subMenu "${HOST_IP}" "${ROOT_PASSWORD}" "${HOST_NAME}" "${USER_NAME}"
		 ;;
		7) copyProfileScript "${HOST_IP}" "${ROOT_PASSWORD}"
			read -p "Click any key..." NULL
			subMenu "${HOST_IP}" "${ROOT_PASSWORD}" "${HOST_NAME}" "${USER_NAME}"
		 ;;
		 8) copySudoersFile "${HOST_IP}" "${ROOT_PASSWORD}"
			read -p "Click any key..." NULL
			subMenu "${HOST_IP}" "${ROOT_PASSWORD}" "${HOST_NAME}" "${USER_NAME}"
		 ;;
		*) echo "Good bye"
		   exit 0 ;;
	esac
fi
}

function addUser() {
	HOST_IP="$1"
	ROOT_PASSWORD="$2"
 	USER_NAME="$3"

	case ${USER_NAME} in
			karsten)  USER_ID="1000" EXTRA_GROUP=""
		 ;;
			michael) USER_ID="1001" EXTRA_GROUP=""
		 ;;
			sascha)  USER_ID="1100" EXTRA_GROUP="-G adm"
		 ;;
			vlad)  USER_ID="1101" EXTRA_GROUP="-G adm"
		 ;;
		*) 	echo "Invalid name ${USER_NAME}"
			echo "Available names are : ${ALL_USERS}"
		   exit 0 ;;
	esac

	echo "Root password: \"${ROOT_PASSWORD}\""
	echo ""
	echo "User password: \"${USERS_PASSWORD}\""
	ssh root@"${HOST_IP}" "if ! id "${USER_NAME}" >/dev/null 2>&1; then /sbin/useradd -m -u ${USER_ID} -g users "${EXTRA_GROUP}" "${USER_NAME}"; fi; /usr/bin/passwd "${USER_NAME}""
}

function updateProfile() {
	HOST_IP="$1"
	HOST_NAME="$2"
	USER_NAME="$3"

	echo "Add alias to the .bash_profile/.profile"

	if [[ -f "${HOME}/.bash_profile" ]]; then
		PROFILE_FILE="${HOME}/.bash_profile"
	fi
	if [[ -f "${HOME}/.profile" ]]; then
		PROFILE_FILE="${HOME}/.profile"
	fi

	case ${USER_NAME} in
			karsten|michael|sascha|vlad)
					if [[ -z "$(grep -w ${HOST_IP} ${PROFILE_FILE})" ]]; then
          				echo "alias ${HOST_NAME}=\"ssh ${USER_NAME}@${HOST_IP}\"" >> ${PROFILE_FILE}
          			else
		            	echo "Entry for server \"${HOST_NAME}\" in ${PROFILE_FILE} already exists"
             		fi
		 ;;

		*) 	echo "Invalid name ${USER_NAME}"
			echo "Available names are : ${ALL_USERS}"
		   exit 0
		 ;;
	esac
}

function createKeys() {
	echo "Create private/public keys pair"
	if [[ ! -d ${HOME}/\.ssh ]]; then
		mkdir -m 700 ${HOME}/.ssh
	fi

	if [[ ! -f ${HOME}/\.ssh/id_rsa\.pub ]]; then
		ssh-keygen -t rsa
	fi
}

function copyKey() {
	echo "Copy public key to the server"
	HOST_ID="$1"
	USER_NAME="$2"

	case ${USER_NAME} in
			karsten|michael|sascha|vlad)
					if [[ -f ${HOME}/.ssh/id_rsa\.pub ]]; then
						echo "User password: \"${USERS_PASSWORD}\""
          				ssh-copy-id -i ${HOME}/.ssh/id_rsa.pub "${USER_NAME}"@"${HOST_ID}"
          			else
		            	echo "Please generate private/public keys pair"
             		fi
		 ;;
		*) 	echo "Invalid name ${USER_NAME}"
			echo "Available names are : ${ALL_USERS}"
		   exit 0 ;;
	esac
}

function createRemoteFolder() {
	HOST_IP="$1"
	ROOT_PASSWORD="$2"
	REMOTE_FOLDER="$3"
	echo "Create remote folder \"${REMOTE_FOLDER}\""

	echo "Root password: \"${ROOT_PASSWORD}\""
	ssh root@"${HOST_IP}" "if ! -d ${REMOTE_FOLDER} >/dev/null 2>&1; then /usr/bin/mkdir -p -m 770 ${REMOTE_FOLDER}; /usr/bin/chown -R vlad:users /opt/work; ln -s /opt/work /work; fi"
}

function installRsync() {
	echo "Install rsync the server"
	HOST_IP="$1"
	ROOT_PASSWORD="$2"
	echo "Root password: \"${ROOT_PASSWORD}\""
	ssh root@"${HOST_IP}" "yum -y install rsync"
}

function copyInstallScripts() {
	echo "Start transfer scripts to the server"
	HOST_IP="$1"
	ROOT_PASSWORD="$2"

	REMOTE_FOLDER="/opt/work/scripts/"

	read -en 1 -p "Create folder \""${REMOTE_FOLDER}"\"? (y/Y) >"
	if [[ "${REPLY-}" == "y" ]] || [[ "${REPLY-}" == "Y" ]]; then
		createRemoteFolder "${HOST_IP}" "${ROOT_PASSWORD}" "${REMOTE_FOLDER}"
	fi

	read -en 1 -p "Install on the server rsync? (y/Y) >"
	if [[ "${REPLY-}" == "y" ]] || [[ "${REPLY-}" == "Y" ]]; then
		installRsync "${HOST_IP}" "${ROOT_PASSWORD}"
	fi

	if [[ -f ./remote_adm.sh ]] || [[ -f ./services_manager.sh ]] || [[ -f ./service_template ]]; then
		echo "Root password: \"${ROOT_PASSWORD}\""
		rsync -amvzO remote_adm.sh services_manager.sh service_template service_sudo_template root@"${HOST_IP}":"${REMOTE_FOLDER}" --chmod=Fug+r,Fug+w,Fug+x,Fo-r,Fo-w,Fo-x --chown=vlad:users --ignore-errors
	fi
}

function copyScriptBootScripts() {
	echo "Start transfer scripts to the server"
	HOST_IP="$1"
	ROOT_PASSWORD="$2"

	read -en 1 -p "Install on the server rsync? (y/Y) >"
	if [[ "${REPLY-}" == "y" ]] || [[ "${REPLY-}" == "Y" ]]; then
		installRsync "${HOST_IP}" "${ROOT_PASSWORD}"
	fi

	if [[ -f ./bash-completion.sh ]] && [[ -f ./deploy.sh ]] && [[ -f ./deploy.sh ]]; then
		REMOTE_FOLDER="/opt/work/app/spring-boot"

		read -en 1 -p "Create folder \""${REMOTE_FOLDER}"\"? (y/Y) >"
		if [[ "${REPLY-}" == "y" ]] || [[ "${REPLY-}" == "Y" ]]; then
			createRemoteFolder "${HOST_IP}" "${ROOT_PASSWORD}" "${REMOTE_FOLDER}"
		fi
			echo "Start transfer spring boot scripts"
			echo "Root password: \"${ROOT_PASSWORD}\""
			rsync -amvzO bash-completion.sh deploy.sh log.sh spring-boot-manager.sh root@"${HOST_IP}":"${REMOTE_FOLDER}" --chmod=Fug+r,Fug+w,Fug+x,Fo-r,Fo-w,Fo-x --chown=vlad:users --ignore-errors
	else
		echo "Some of files: bash-completion.sh deploy.sh log.sh is absent"
	fi

	if [[ -f ./spring-boot ]]; then
		REMOTE_FOLDER="/opt/work/app/server"

		read -en 1 -p "Create folder \""${REMOTE_FOLDER}"\"? (y/Y) >"
		if [[ "${REPLY-}" == "y" ]] || [[ "${REPLY-}" == "Y" ]]; then
			createRemoteFolder "${HOST_IP}" "${ROOT_PASSWORD}" "${REMOTE_FOLDER}"
		fi
			echo "Start transfer spring boot scripts"
			echo "Root password: \"${ROOT_PASSWORD}\""
			rsync -amvzO spring-boot root@"${HOST_IP}":"${REMOTE_FOLDER}" --chmod=Fug+r,Fug+w,Fug+x,Fo-r,Fo-w,Fo-x --chown=vlad:users --ignore-errors
	else
		echo "Some of files: spring-boot is absent"
	fi
}

function copyProfileScript() {
	echo "Copy profile script to the server"
	HOST_IP="$1"
	ROOT_PASSWORD="$2"

	read -en 1 -p "Install on the server rsync? (y/Y) >"
	if [[ "${REPLY-}" == "y" ]] || [[ "${REPLY-}" == "Y" ]]; then
		installRsync "${HOST_IP}" "${ROOT_PASSWORD}"
	fi

	if [[ -f ./zzz_netcupers.sh ]]; then
		echo "Root password: \"${ROOT_PASSWORD}\""
		rsync -amvzO zzz_netcupers.sh root@"${HOST_IP}":/etc/profile.d/ --chmod=Fugo+r,Fu+w,Fgo-w,Fugo-x --ignore-errors
	fi
}

function copySudoersFile() {
	echo "Copy sudoers file to the server"
	HOST_IP="$1"
	ROOT_PASSWORD="$2"

	read -en 1 -p "Install on the server rsync? (y/Y) >"
	if [[ "${REPLY-}" == "y" ]] || [[ "${REPLY-}" == "Y" ]]; then
		installRsync "${HOST_IP}" "${ROOT_PASSWORD}"
	fi

	if [[ -f ./netcup_sudoers ]]; then
		echo "Root password: \"${ROOT_PASSWORD}\""
		rsync -amvzO netcup_sudoers root@"${HOST_IP}":/etc/sudoers.d/ --chmod=Fug+r,Fu+w,Fo-r,Fgo-w,Fugo-x --ignore-errors
	fi

}

################################################################################

clear
echo ""
#echo "${boldFont}"
echo "################################################################################"
echo "#                             NETCUP Sever Local Manager ®                     #"
echo "#                 A program for set up local and remote configuration          #"
echo "################################################################################"
#echo "${normal}"
echo ""
COUNT=1
for HOST in ${SERVER_LIST}; do
	printf "${COUNT}.\t${HOST}\n"
	((COUNT++))
done
printf "\n"
printf "Exit - any key\n"
echo ""
echo ""
read -en 1 -p "Please select a server name > "
case ${REPLY-} in
	1)  subMenu "${NETCUP_DEV_IP}" "${NETCUP_DEV_ROOT_PASSWORD}" "netcup_dev" ""
	 ;;
	2)  subMenu "${NETCUP_STAGE_IP}" "${NETCUP_STAGE_ROOT_PASSWORD}" "netcup_stage" ""
	 ;;
	3)  subMenu "${NETCUP_WEB_IP}" "${NETCUP_WEB_ROOT_PASSWORD}" "netcup_web" ""
	 ;;
	*) echo "Good bye"
	   exit 0 ;;
esac

https://tunnelblick.net/downloads.html
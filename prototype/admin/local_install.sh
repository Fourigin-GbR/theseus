#!/bin/sh

########################### VARIABLE ###########################################
BASEDIR=$(realpath .)

normal=`echo "\033[0m"`
underscoreFont=`echo "\033[4m"`
boldFont=`echo "\033[1m"`
fgGreen=`echo "\033[32m"`
fgRed=`echo "\033[31m"`

USERS_LIST="karsten michael simon"
ADM_LIST="sascha vlad"
ALL_USERS="karsten michael sascha simon vlad"

USERS_PASSWORD="GeBeEr2015"
NETCUP_DEV_ROOT_PASSWORD=""
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
			karsten|michael|sascha|simon|vlad)
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
	echo "5. Copy scripts to the server"
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
		5) copyScripts "${HOST_IP}" "${ROOT_PASSWORD}"
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
			simon)  USER_ID="1002" EXTRA_GROUP=""
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
	ssh root@"${HOST_IP}" "if ! id "${USER_NAME}" >/dev/null 2>&1; then /sbin/useradd -m -u ${USER_ID} -g users "${EXTRA_GROUP}" "${USER_NAME}"; fi"
}

function updateProfile() {
	HOST_IP="$1"
	HOST_NAME="$2"
	USER_NAME="$3"

	echo "Update the .bash_profile"

	case ${USER_NAME} in
			karsten|michael|sascha|simon|vlad)
					if [[ -z "$(grep -w ${HOST_IP} ${HOME}/.bash_profile)" ]]; then
          				echo "alias ${HOST_NAME}=\"ssh ${USER_NAME}@${HOST_IP}\"" >> ${HOME}/.bash_profile
          			else
		            	echo "Entry for server \"${HOST_NAME}\" in .bash_profile already exists"
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
			karsten|michael|sascha|simon|vlad)
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
	echo "Create /opt/work/scripts folder"
	HOST_IP="$1"
	ROOT_PASSWORD="$2"

	echo "Root password: \"${ROOT_PASSWORD}\""
	ssh root@"${HOST_IP}" "if ! -d /opt/work/scripts >/dev/null 2>&1; then /usr/bin/mkdir -p -m 770 /opt/work/scripts; /usr/bin/chown -R vlad:adm /opt/work/scripts; ln -s /opt/work /work; fi"
}

function installRsync() {
	echo "Install rsync the server"
	HOST_IP="$1"
	ROOT_PASSWORD="$2"
	echo "Root password: \"${ROOT_PASSWORD}\""
	ssh root@"${HOST_IP}" "yum -y install rsync"
}

function copyScripts() {
	echo "Copy scripts to the server"
	HOST_IP="$1"
	ROOT_PASSWORD="$2"

	createRemoteFolder "${HOST_IP}" "${ROOT_PASSWORD}"
	read -en 1 -p "Install on the server rsync? (y/Y) >"
	if [[ "${REPLY-}" == "y" ]] || [[ "${REPLY-}" == "Y" ]]; then
		installRsync "${HOST_IP}" "${ROOT_PASSWORD}"
	fi

	if [[ -f ./zzz_netcupers.sh ]]; then
		echo "Root password: \"${ROOT_PASSWORD}\""
		rsync -amvzO zzz_netcupers.sh root@"${HOST_IP}"/etc/profile.d/ --chmod=Fugo+r,Fu+w,Fgo-w,Fugo-x --ignore-errors
	fi

	if [[ -f ./remote_adm.sh ]] || [[ -f ./remote_root.sh ]]; then
		echo "Root password: \"${ROOT_PASSWORD}\""
		rsync -amvzO remote_*.sh root@"${HOST_IP}"/opt/work/scripts/ --chmod=Fug+r,Fug+w,Fug+x,Fo-r,Fo-w,Fo-x --owner=vlad:adm --ignore-errors
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
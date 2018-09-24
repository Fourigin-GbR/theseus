#!/bin/sh

################################## CONSTANTS ###################################
LOCALES_LIST="./locales_list.txt"
ROOT_DIR="/opt/shared"
GREEN='\033[0;32m'
RED='\033[0;31m'
RESET='\033[0m' # No Color
################################################################################

clear

    echo -e "Verifying global directories"

    ASSETS_DIR=$ROOT_DIR/assets
	if [ ! -d $ASSETS_DIR ]; then
		echo -e "    $ASSETS_DIR ${RED}does not exist${RESET}"
		mkdir $ASSETS_DIR
	else
		echo -e "    $ASSETS_DIR ${GREEN}already exist${RESET}"
	fi

    TARGETS_DIR=$ROOT_DIR/target

echo -e "Verifying docroots"

while read locale; do
	echo "$locale"

	LOCALE_DIR=$TARGETS_DIR/$locale
	if [ ! -d "$LOCALE_DIR" ]; then
		echo -e "    $LOCALE_DIR ${RED}does not exist${RESET}"
		echo -e "     - creating directory $LOCALE_DIR"
		mkdir $LOCALE_DIR
		chmod 775 $LOCALE_DIR
    else
		echo -e "    $LOCALE_DIR ${GREEN}already exist${RESET}"
	fi

    ASSETS_DIR=$LOCALE_DIR/assets
	if [ ! -d "$ASSETS_DIR" ]; then
		echo -e "    $ASSETS_DIR ${RED}does not exist${RESET}"
		echo -e "     - creating directory $ASSETS_DIR"
		mkdir $ASSETS_DIR
		chmod 775 $ASSETS_DIR
	else
		echo -e "    $ASSETS_DIR ${GREEN}already exist${RESET}"
	fi

    SHARED_ASSETS_LINK=$LOCALE_DIR/shared
	if [ ! -e "$SHARED_ASSETS_LINK" ]; then
		echo -e "    $SHARED_ASSETS_LINK ${RED}does not exist${RESET}"
		echo -e "     - creating soft link to shared assets"
        cd $LOCALE_DIR
		ln -s ../../assets/ shared
        cd -
    else
		echo -e "    $SHARED_ASSETS_LINK ${GREEN}already exist${RESET}"
	fi

done < "$LOCALES_LIST"

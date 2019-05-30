#!/bin/bash
#curl "http://argo.greekestate.fourigin.com/cms/compile/write-output?locale=EN&path=/objects/search&mode=LIVE&flush=true&recursive=true"
#curl "http://argo.greekestate.fourigin.com/cms/compile/write-output?locale=DE&path=/objects/search&mode=LIVE&flush=true&recursive=true"
#curl "http://argo.greekestate.fourigin.com/cms/compile/write-output?locale=RU&path=/objects/search&mode=LIVE&flush=true&recursive=true"
#curl "http://argo.greekestate.fourigin.com/cms/compile/write-output?locale=EN&path=/home&mode=LIVE&flush=true"
#curl "http://argo.greekestate.fourigin.com/cms/compile/write-output?locale=DE&path=/home&mode=LIVE&flush=true"
#curl "http://argo.greekestate.fourigin.com/cms/compile/write-output?locale=RU&path=/home&mode=LIVE&flush=true"
#http://argo.greekestate.fourigin.com/cms/compile/write-output?locale=RU&path=/impressum&mode=LIVE&flush=true
#http://argo.greekestate.fourigin.com/cms/compile/write-output?locale=EN&path=/about&mode=LIVE&flush=true
#http://argo.greekestate.fourigin.com/cms/compile/write-output?locale=RU&path=/contact&mode=LIVE&flush=true

cd /work/www/bestgreekestate.com
rsync -xvrc vlad@fourigin.de:/home/vlad/argo/www/greekestate.en/*.html .
rsync -xvrc vlad@fourigin.de:/home/vlad/argo/www/greekestate.en/objects .

cd /work/www/de.bestgreekestate.com
rsync -xvrc vlad@fourigin.de:/home/vlad/argo/www/greekestate.de/*.html .
rsync -xvrc vlad@fourigin.de:/home/vlad/argo/www/greekestate.de/objekte .

cd /work/www/ru.bestgreekestate.com
rsync -xvrc vlad@fourigin.de:/home/vlad/argo/www/greekestate.ru/*.html .
rsync -xvrc vlad@fourigin.de:/home/vlad/argo/www/greekestate.ru/objekti .

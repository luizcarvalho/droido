adb -d shell 'run-as br.com.redrails.torpedos cat /data/data/br.com.redrails.torpedos/databases/database.sqlite > /sdcard/database.sqlite'
adb pull /sdcard/database.sqlite
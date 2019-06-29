import bme280
import smbus2
from time import sleep

port = 1
address = 0x77
bus = smbus2.SMBus(port)

bme280.load_calibration_params(bus,address)

while True:
    bme280_data = bme280.sample(bus,address)
    time = bme280_data.timestamp
    humidity = bme280_data.humidity
    pressure = bme280_data.pressure
    temperature = bme280_data.temperature
    print(time)
    print(temperature,humidity,pressure)
    try:
        f = open("weather_data.dat",'a')
        f.write(time.__str__() + " ")
        f.write(temperature.__str__() + " ")
        f.write(humidity.__str__() + " ")
        f.write(pressure.__str__() + "\n")
    finally:
        f.close()
    try:
        f = open("current_weather_data.dat", 'w')
        f.write(time.__str__() + " ")
        f.write(temperature.__str__() + " ")
        f.write(humidity.__str__() + " ")
        f.write(pressure.__str__())
    finally:
        f.close()
    try:
        f = open("/var/www/html/index.nginx-debian.html",'w')
        f.write("<!DOCTYPE html>\n<html>\n<head>\n")
        f.write("<title>bme280 Test Data</title>\n")
        f.write("<style>\nbody {\nwidth: 35em;\nmargin: 0 auto;\n")
        f.write("font-family: Tahoma, Veranda, Ariel, sans-serif;\n")
        f.write("}\n</style>\n</head>\n")
        f.write("<body>\n<p>")
        f.write(time.__str__() + " ")
        f.write(temperature.__str__() + " ")
        f.write(humidity.__str__() + " ")
        f.write(pressure.__str__() + "</p>\n")
        f.write("</body>\n</html>");
    finally:
        f.close()
    sleep(600) #collect every 10 minutes

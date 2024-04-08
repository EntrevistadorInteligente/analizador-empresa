from selenium.webdriver.common.by import By
from configuration_driver import setup_driver
import time
import random
#a
def extraer_enlaces_y_guardar(url_base, parametro_inicio, parametro_fin, nombre_archivo):
    driver = setup_driver()

    enlaces_unicos = set()
    try:
        with open(nombre_archivo, "r") as archivo:
            for linea in archivo:
                enlaces_unicos.add(linea.strip())
    except FileNotFoundError:
        pass

    for i in range(parametro_inicio, parametro_fin + 1):
        url_con_parametro = f"{url_base}?p={i}"
        try:
            driver.get(url_con_parametro)
            enlaces = driver.find_elements(By.CSS_SELECTOR, "a.js-o-link")

            with open(nombre_archivo, "a") as archivo:
                for enlace in enlaces:
                    href = enlace.get_attribute('href')
                    print(href)
                    if href not in enlaces_unicos:
                        archivo.write(href + "\n")
                        enlaces_unicos.add(href)

            print(f"Se han extraído y guardado los enlaces de la página {i} en '{nombre_archivo}' sin duplicados.")

        finally:
            if i == parametro_fin:
                driver.quit()

        tiempo_espera = random.randint(5, 10)
        print(f"Esperando {tiempo_espera} segundos antes de procesar la siguiente página...")
        time.sleep(tiempo_espera)

url_base = "https://co.computrabajo.com/empresas/buscador"
nombre_archivo = "enlaces.txt"
extraer_enlaces_y_guardar(url_base, 1, 500, nombre_archivo)

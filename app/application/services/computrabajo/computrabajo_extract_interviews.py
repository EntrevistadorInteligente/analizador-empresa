from selenium.common import TimeoutException
from configuration_driver import setup_driver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import time
import csv
import os


def leer_urls_csv(nombre_archivo_csv='informacion_empresas.csv'):
    urls = []
    with open(nombre_archivo_csv, mode='r', newline='', encoding='utf-8') as archivo:
        lector = csv.DictReader(archivo)
        for fila in lector:
            if fila['Industry'] in ['Informática / Software', 'Informática / Hardware']:
                urls.append(fila['URL'])
    return urls

def navegar_a_entrevistas(driver):
    try:
        entrevistas_tab = WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.XPATH, "//a[@title='Entrevistas']"))
        )
        if "sel" not in entrevistas_tab.get_attribute("class"):
            entrevistas_tab.click()
    except:
        print("El elemento de la pestaña Entrevistas no se encontró después de 10 segundos.")
        return False
    return True

def extraer_entrevistas(driver):
    entrevistas_info = []
    try:
        entrevistas = WebDriverWait(driver, 10).until(
            EC.presence_of_all_elements_located((By.CSS_SELECTOR, "div[name='interviews']"))
        )
    except TimeoutException:
        try:
            entrevistas = WebDriverWait(driver, 10).until(
                EC.presence_of_all_elements_located((By.CSS_SELECTOR, "div[title='interviews']"))
            )
        except TimeoutException:
            print("Los elementos de entrevistas no se encontraron después de 10 segundos.")
            return False

    if not entrevistas:
        print("No se encontraron entrevistas en esta página.")
        return False

    for entrevista in entrevistas:
        id_entrevista = entrevista.get_attribute("id")
        print(f"ID de la entrevista: {id_entrevista}")

        titulo = entrevista.find_element(By.CSS_SELECTOR, "p.fwB.fs18").text
        print("Titulo: ", titulo)

        ubicacion_y_fecha = entrevista.find_element(By.CSS_SELECTOR, "p.fc_aux.fs15.mt5").text
        print("Ubicacion y fecha: ", ubicacion_y_fecha)

        valoraciones_elements = entrevista.find_elements(By.CSS_SELECTOR, "ul.mt10.mb10 li")
        valoraciones_union = ";".join([elem.text for elem in valoraciones_elements])
        print("Valoraciones", valoraciones_union)

        descripcion = entrevista.find_element(By.CSS_SELECTOR, "p.w80.w100_m").text
        print("Descripcion: ", descripcion)

        canal_element = entrevista.find_element(By.CSS_SELECTOR, "p.fwB.mt15")
        try:
            next_sibling = canal_element.find_element(By.XPATH, "./following-sibling::*").text
            if next_sibling:
                print("Canal de Entrevista: ", next_sibling.text)
        except:
            print("No se encontró el siguiente elemento hermano.")

        etapas_de_la_entrevista_elements = entrevista.find_elements(By.CSS_SELECTOR, "ul.pl20")
        etapas_union = ";".join([etapa.text for etapa in etapas_de_la_entrevista_elements])
        print("Etapas de la entrevista: ", etapas_union)

        preguntas_en_la_entrevista = entrevista.find_elements(By.CSS_SELECTOR, "div.w80.w100_m")
        preguntas_union = ";".join([pregunta.text for pregunta in preguntas_en_la_entrevista])

        print("Preguntas en la Entrevista: ", preguntas_union)


        utilidad_si = entrevista.find_element(By.CSS_SELECTOR, "span[data-help_s]").text
        utilidad_no = entrevista.find_element(By.CSS_SELECTOR, "span[data-help_n]").text
        print(f"¿Esta opinión ha sido útil? Sí: {utilidad_si.split()[-1]}, No: {utilidad_no.split()[-1]}")
        entrevistas_info.append({
            'id_entrevista': id_entrevista,
            'titulo': titulo,
            'ubicacion_y_fecha': ubicacion_y_fecha,
            'valoraciones': valoraciones_union,
            'descripcion': descripcion,
            'canal_de_entrevista': next_sibling,
            'etapas_de_la_entrevista': etapas_union,
            'preguntas_en_la_entrevista': preguntas_union,
            'utilidad_si': utilidad_si.split()[-1],
            'utilidad_no': utilidad_no.split()[-1],
        })
    return entrevistas_info

def navegar_y_extraer_por_paginas(driver, url):
    nombre_empresa = url.split('/')[-1]
    nombre_archivo = f"data/{nombre_empresa}.csv"
    os.makedirs(os.path.dirname(nombre_archivo), exist_ok=True)

    ids_existentes = set()
    if os.path.exists(nombre_archivo) and os.path.getsize(nombre_archivo) > 0:
        with open(nombre_archivo, mode='r', newline='', encoding='utf-8') as archivo:
            lector = csv.DictReader(archivo)
            for fila in lector:
                ids_existentes.add(fila['id_entrevista'])

    numero_de_pagina = 1
    while True:
        if not navegar_a_entrevistas(driver):
            print(f"No se pudo acceder a la sección de entrevistas de la página {numero_de_pagina}.")
            break

        time.sleep(5)
        driver.get(driver.current_url.split('?')[0] + f"?p={numero_de_pagina}")

        entrevistas = extraer_entrevistas(driver)
        if entrevistas:
            with open(nombre_archivo, mode='a', newline='', encoding='utf-8') as archivo:
                fieldnames = ['id_entrevista', 'titulo', 'ubicacion_y_fecha', 'valoraciones', 'descripcion', 'canal_de_entrevista', 'etapas_de_la_entrevista', 'preguntas_en_la_entrevista', 'utilidad_si', 'utilidad_no']
                escritor = csv.DictWriter(archivo, fieldnames=fieldnames)
                if numero_de_pagina == 1 and not ids_existentes:
                    escritor.writeheader()
                for entrevista in entrevistas:
                    if entrevista['id_entrevista'] not in ids_existentes:
                        escritor.writerow(entrevista)
                        ids_existentes.add(entrevista['id_entrevista'])
        else:
            print(f"No se encontraron más entrevistas en la página {numero_de_pagina}. Terminando...")
            break

        numero_de_pagina += 1

if __name__ == "__main__":
    driver = setup_driver()
    urls = leer_urls_csv()
    for url in urls:
        driver.get(url)
        navegar_y_extraer_por_paginas(driver, url)
    driver.quit()



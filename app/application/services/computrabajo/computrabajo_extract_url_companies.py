from selenium.webdriver.common.by import By
from configuration_driver import setup_driver
import csv
import time
import random

def leer_urls_de_archivo(ruta_archivo):
    with open(ruta_archivo, 'r', encoding='utf-8') as archivo:
        return [linea.strip() for linea in archivo if linea.strip()]

def extraer_informacion_y_guardar_csv(nombre_archivo_urls, parametro_inicio, parametro_fin, nombre_archivo_csv):
    urls_base = leer_urls_de_archivo(nombre_archivo_urls)
    driver = setup_driver()
    urls_procesadas = set()

    subdominio_pais = {
        'co': 'Colombia',
        'ar': 'Argentina',
        'cl': 'Chile',
        'ec': 'Ecuador',
        'ni': 'Nicaragua',
        'uy': 'Uruguay',
        'mx': 'Mexico',
        'sv': 'El Salvador',
        'pa': 'Panama',
        've': 'Venezuela',
        'pe': 'Peru',
        'bo': 'Bolivia',
        'gt': 'Guatemala',
        'py': 'Paraguay',
        'do': 'Republica Dominicana',
        'cr': 'Costa Rica',
        'cu': 'Cuba',
        'hn': 'Honduras',
        'pr': 'Puerto Rico'
    }

    try:
        with open(nombre_archivo_csv, mode='r', newline='', encoding='utf-8') as archivo_csv:
            lector_csv = csv.reader(archivo_csv, delimiter=',', quotechar='"')
            next(lector_csv, None)
            for fila in lector_csv:
                urls_procesadas.add(fila[0])
    except FileNotFoundError:
        pass

    with open(nombre_archivo_csv, mode='a', newline='', encoding='utf-8') as archivo_csv:
        escritor_csv = csv.writer(archivo_csv, delimiter=',', quotechar='"', quoting=csv.QUOTE_MINIMAL)
        if not urls_procesadas:
            escritor_csv.writerow(['URL', 'Company Name', 'City_Region', 'Country', 'Industry', 'Puestos', 'Reviews'])

        for url_base in urls_base:
            contador_url_base = 0
            contador_url_base += 1
            print(f"{contador_url_base} / {len(urls_base)} - Url Base: {url_base}")
            country_code = url_base.split('.')[0].split('//')[-1]
            country = subdominio_pais.get(country_code, 'No disponible')

            for i in range(parametro_inicio, parametro_fin + 1):
                url_con_parametro = f"{url_base}?p={i}"
                print(f"Pagina = {i} / {parametro_fin}")

                try:
                    driver.get(url_con_parametro)
                    time.sleep(3)
                    articulos = driver.find_elements(By.CSS_SELECTOR, "article.box_p")
                    if not articulos:
                        print(f"No se encontraron artículos en la página {i} de {url_base}. Se detiene la búsqueda.")
                        break
                    for articulo in articulos:
                        url = articulo.find_element(By.CSS_SELECTOR, "a.js-o-link").get_attribute('href')
                        if url not in urls_procesadas:
                            urls_procesadas.add(url)

                            company_name = articulo.find_element(By.CSS_SELECTOR, "h2 a.js-o-link").text

                            try:
                                location_element = articulo.find_element(By.XPATH, ".//li[div[contains(@class, 'icon local')]]")
                                city_region = location_element.text
                            except Exception:
                                city_region = "No disponible"

                            industry = "No disponible"
                            all_potential_industries = articulo.find_elements(By.XPATH, ".//li[div[contains(@class, 'icon')]]")
                            for element in all_potential_industries:
                                div_class = element.find_element(By.TAG_NAME, "div").get_attribute("class")
                                if div_class == "icon gent":
                                    industry = element.text
                                    break

                            try:
                                puestos_element = articulo.find_element(By.XPATH, ".//li[div[contains(@class, 'icon gent_persona')]]")
                                puestos = puestos_element.text.split(' ')[0]
                            except Exception:
                                puestos = "No disponible"

                            try:
                                reviews_element = articulo.find_element(By.CSS_SELECTOR, "a.fl.w_100.empr.it-blank span.valoracions")
                                reviews = reviews_element.text.split(' ')[0]
                            except Exception:
                                reviews = "No disponible"

                            escritor_csv.writerow([url, company_name, city_region, country, industry, puestos, reviews])

                finally:
                    if url_base == urls_base[-1] and i == parametro_fin:
                        driver.quit()

                tiempo_espera = random.randint(5, 10)
                print(f"Esperando {tiempo_espera} segundos antes de procesar la siguiente página...")
                time.sleep(tiempo_espera)

nombre_archivo_urls = "computrabajo_input_urls.txt"
nombre_archivo_csv = "informacion_empresas.csv"
parametro_inicio = 1
parametro_fin = 500
extraer_informacion_y_guardar_csv(nombre_archivo_urls, parametro_inicio, parametro_fin, nombre_archivo_csv)

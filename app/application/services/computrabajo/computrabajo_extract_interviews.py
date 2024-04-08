from configuration_driver import setup_driver
from selenium.webdriver.common.by import By

def extraer_entrevistas(driver):
    entrevistas = driver.find_elements(By.CSS_SELECTOR, "div[name='interviews']")
    for entrevista in entrevistas:
        titulo = entrevista.find_element(By.CSS_SELECTOR, "p.fwB.fs18").text
        print(titulo)
        detalles = entrevista.find_element(By.CSS_SELECTOR, "p.fc_aux.fs15.mt5").text
        print(detalles)

        # Extracción de las etiquetas de valoración, dificultad, y respuesta
        valoraciones = entrevista.find_elements(By.CSS_SELECTOR, "ul.mt10.mb10 li")
        for valoracion in valoraciones:
            print(valoracion.text)

        descripcion = entrevista.find_element(By.CSS_SELECTOR, "p.w80.w100_m").text
        print(descripcion)
        print("----")
        canal_element = entrevista.find_element(By.CSS_SELECTOR, "p.fwB.mt15")
        next_sibling = canal_element.find_element(By.XPATH, "./following-sibling::*")
        if next_sibling:
            print(next_sibling.text)

        print("----")
        etapas_de_la_entrevista = entrevista.find_elements(By.CSS_SELECTOR, "ul.pl20")
        for etapas in etapas_de_la_entrevista:
            print(etapas.text)
        print("----")
        preguntas_en_la_entrevista = entrevista.find_elements(By.CSS_SELECTOR, "div.w80.w100_m")
        for pregunta in preguntas_en_la_entrevista:
            print(pregunta.text)

        # Encuentra todos los elementos span con la clase "fr fc_base" dentro del elemento entrevista
        elementos_span = entrevista.find_elements(By.XPATH, ".//span[@class='fr fc_base']")

        for elemento in elementos_span:
            texto_elemento = elemento.text

            padre_elemento = elemento.find_element(By.XPATH, "..")

            texto_padre_elemento = padre_elemento.text

            data_help = None
            if padre_elemento.get_attribute("data-help_s"):
                data_help = padre_elemento.get_attribute("data-help_s")
            elif padre_elemento.get_attribute("data-help_n"):
                data_help = padre_elemento.get_attribute("data-help_n")

            print("-----")
            print("Texto del elemento:", texto_elemento)
            print("Texto del padre del elemento:", texto_padre_elemento)
            print("Valor de data-help_s o data-help_n del elemento padre:", data_help)
            print("-----")



driver = setup_driver()
driver.get("https://co.computrabajo.com/adecco-colombia-sa/entrevistas")
extraer_entrevistas(driver)



### Escuela Colombiana de Ingeniería

### Arquitecturas de Software


## Antes de hacer este ejercicio, realice [el ejercicio introductorio al manejo de Spring y la configuración basada en anotaciones](https://github.com/ARSW-ECI/Spring_LightweightCont_Annotation-DI_Example).



#### API REST adaptable para el cálculo de cuentas de restaurantes.

En este proyecto se va a construír un API REST que permita calcular el valor total de una cuenta de restaurante, teniendo en cuenta las políticas y regímenes tributarios configurados para la misma.

Este API será soportado por el siguiente modelo de clases, el cual considera el principio de inversión de dependencias, y asume el uso de Inyección de dependencias:

![](img/ClassDiagram.png)


El anterior modelo considera, por ahora, los siguientes casos (la aplicación podrá configurarse de acuerdo con el restaurante donde sea usado):

* En algunos restaurantes -ilegales- los precios de los platos NO tienen gravamen alguno (BasicBillCalculator).
* En muchos otros se cobra el IVA, pero de dos maneras diferentes:
	* 16% estándar sobre todos los productos (CalcularodCuentaConIVA + VerificadorIVAEstandar).
	* Con la reforma tributaria de 2016, aplicando un IVA diferencial al tipo de producto: 16% para las bebidas y 19% para los platos.


Por defecto, el manejador de órdenes tiene dos órdenes registradas para las mesas #1 y #3:


* Orden Mesa 1:

	| Producto      | Cantidad | Precio Unitario          | 
	| ------------- | ----- |:-------------:| 
	|PIZZA|3|$10000|
	|HOTDOG|1|$3000|
	|COKE|4|$1300|


* Orden Mesa 3:

	| Producto      | Cantidad | Precio  Unitario         | 
	| ------------- | ----- |:-------------:| 
	|HAMBURGER|2|$12300|
	|COKE|2|$1300|




### Parte I

1. Configure su aplicación para que ofrezca el recurso "/orders", de manera que cuando se le haga una petición GET, retorne -en formato jSON- el conjunto de todas las órdenes. Para esto:
	* Modifique la clase OrdersAPIController teniendo en cuenta el siguiente ejemplo de controlador REST hecho con SpringMVC/SpringBoot:

		```java
		@RestController
		@RequestMapping(value = "/url-raiz-recurso")
		public class XXController {
			@RequestMapping(method = RequestMethod.GET)
			public ResponseEntity<?> manejadorGetRecursoXX(){
				try {
					//obtener datos que se enviarán a través del API
					return new ResponseEntity<>(data,HttpStatus.ACCEPTED);
				} catch (XXException ex) {
					Logger.getLogger(XXController.class.getName()).log(Level.SEVERE, null, ex);
					return new ResponseEntity<>("Error bla bla bla",HttpStatus.NOT_FOUND);
				}  
			}      
		}

		```
	* Haga que en esta misma clase se inyecte el bean de tipo RestaurantOrderServices, y que a éste -a su vez-, se le inyecte el bean BasicBillCalculator. Para esto, revise en [la documentación de Spring](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/beans.html) las secciones 7.9.2 y 7.10.3, respecto a cómo declarar Beans, y cómo definir la inyección de dependencias entre éstos, mediante anotaciones.

2. Verifique el funcionamiento de a aplicación lanzando la aplicación con maven:

	```bash
	$ mvn spring-boot:run
	
	```
	Y luego enviando una petición GET a: http://localhost:8080/orders. Rectifique que, como respuesta, se obtenga un objeto jSON con una lista que contenga las dos órdenes disponibles por defecto.


3. Modifique el controlador para que ahora, adicionalmente, acepte peticiones GET al recurso /orden/{idmesa}, donde {idmesa} es el número de una mesa en particular. En este caso, la respuesta debe ser la orden que corresponda a la mesa indicada en formato jSON, o un error 404 si la mesa no existe o no tiene una orden asociada. Para esto, revise en [la documentación de Spring](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html), sección 22.3.2, el uso de @PathVariable. De nuevo, verifique que al hacer una petición GET -por ejemplo- a recurso http://localhost:8080/orders/1, se obtenga en formato jSON el detalle correspondiente a la orden de la mesa 1.


### Parte II

1.  Agregue el manejo de peticiones POST (creación de nuevas ordenes), de manera que un cliente http pueda registrar una nueva orden haciendo una petición POST al recurso ‘ordenes’, y enviando como contenido de la petición todo el detalle de dicho recurso a través de un documento jSON. Para esto, tenga en cuenta el siguiente ejemplo, que considera -por consistencia con el protocolo HTTP- el manejo de códigos de estados HTTP (en caso de éxito o error):

	```	java
		@RequestMapping(method = RequestMethod.POST)	
		public ResponseEntity<?> manejadorPostRecursoXX(@RequestBody TipoXX o){
			try {
				//registrar dato
				return new ResponseEntity<>(HttpStatus.CREATED);
			} catch (XXException ex) {
				Logger.getLogger(XXController.class.getName()).log(Level.SEVERE, null, ex);
				return new ResponseEntity<>("Error bla bla bla",HttpStatus.FORBIDDEN);            
			}        
		
		}
	```	


2.  Para probar que el recurso ‘ordenes’ acepta e interpreta
    correctamente las peticiones POST, use el comando curl de Unix. Este
    comando tiene como parámetro el tipo de contenido manejado (en este
    caso jSON), y el ‘cuerpo del mensaje’ que irá con la petición, lo
    cual en este caso debe ser un documento jSON equivalente a la clase
    Cliente (donde en lugar de {ObjetoJSON}, se usará un objeto jSON correspondiente a una nueva orden:

	```	
	$ curl -i -X POST -HContent-Type:application/json -HAccept:application/json http://URL_del_recurso_ordenes -d '{ObjetoJSON}'
	```	

	Con lo anterior, registre una nueva orden (para 'diseñar' un objeto jSON, puede usar [esta herramienta](http://www.jsoneditoronline.org/)):


	|  Producto      | Cantidad |
	| ------------- | ----- |
	|  HAMBURGER|2|
	|  PIZZA|3|$10000|
	|  BEER|2|$2500|

	Nota: puede basarse en el formato jSON mostrado en el navegador al consultar una orden con el método GET.


3. Teniendo en cuenta el número de mesa asociado a la nueva orden, verifique que la misma se pueda obtener mediante una petición GET al recurso '/orders/{idmesa}' correspondiente.


### Parte III


4. Haga lo necesario para que ahora el API acepte peticiones al recurso '/orders/{idmesa}/total, las cuales retornen el total de la cuenta de la orden {idorden}.

5. Una vez hecho esto, rectifique que el esquema de inyección de dependencias funcione correctamente. Cambie la configuración para que ahora se use BillWithTaxesCalculator, con TaxesCalculator2016ColTributaryReform. Compruebe que para las mesas 1 y 3 se calcule el total de forma diferente.

1. Se sabe que el desarrollo dirigido por pruebas es fundamental si se quiere lograr un producto robusto. En este caso, NO hay pruebas para los componentes de cálculo de costos. Revise la clase ApplicationServicesTest, la cual tiene las anotaciones necesarias para ejecutar pruebas en el contexto de Spring (es decir, inyectando dependencias). Sobre la misma:
	* Use la anotación @Autowired para que a la prueba se le inyecte el bean de tipo RestaurantOrderServices.
	* Suponiendo que la configuración de cálculo que se usará será: 
	 BillWithTaxesCalculator + TaxesCalculator2016ColTributaryReform, agregue unos casos de prueba (a partir de unas clases de equivalencia) para probar el método   __calculateTableBill(int tableNumber)__. 


### Parte IV

1. Se requiere que el API permita agregar un producto a una orden. Revise [acá](http://restcookbook.com/HTTP%20Methods/put-vs-post/) cómo se debe manejar el verbo PUT con este fin, y haga la implementación en el proyecto.
2. Se requiere que el API permita cancelar la orden de una mesa. Agregue esta funcionalidad teniendo en cuenta que de acuerdo con el estilo REST, ésto se debería poder hacer usando el verbo DELETE en el recurso /orders/{idmesa}.
3. Analice: qué error, derivado de una condición de carrera, se podría presentar con este API teniendo en cuenta que:
	* En la configuración actual de la aplicación, que usa Tomcat como servidor de aplicaciones, se crea un hilo de ejecución para cada cliente que se conecte.
	* Muchos clientes del API podrán consultar cuentas, consultar totales, modificar y cerrar cuentas concurrentemente.
Escriba su análisis en el archivo ANALISIS_CONCURRENCIA.txt

	
<!--### Criterios de evaluación

1. Se pueden crear nuevas órdenes, mediante POST.
2. Se puede calcular el valor de la orden, mediante GET.
3. La aplicación permite cambiar la estrategia de cálculo del valor de la orden (cambiando las anotaciones @Service).
4. La aplicación permite actualizar las órdenes mediante PUT.
5. En análisis de las posibles condiciones de carrera es consistente.-->



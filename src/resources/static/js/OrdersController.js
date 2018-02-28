var ordersL = [];
var initialOrder = '{ "order_id": 1, "table_id": 1, "products": [{ "product": "PIZZA", "quantity": 3,"price": "$15.000"}, {"product": "HAMBURGER","quantity": 1,"price": "$12.300"}]}';
var initialOrdObj  = JSON.parse(initialOrder);
ordersL[0] = initialOrdObj;
var newOrder = '{ "order_id": 2, "table_id": 1, "products": [{ "product": "COKE", "quantity": 2,"price": "$15.000"}, {"product": "PIZZA","quantity": 2,"price": "$12.300"}]}';
var newOrdObj  = JSON.parse(newOrder);
ordersL[1] = newOrdObj;
function generarTabla() {
  var body = document.getElementsByTagName("main")[0];
  var tabla   = document.createElement("table");
  var tblBody = document.createElement("tbody");
  for (var i = 0; i <= initialOrdObj["products"].length ; i++) {
    var hilera = document.createElement("tr");
    for (var j = 0; j < 4; j++) {
      var celda = document.createElement("td");
			if(i == 0){
				if(j==0){
					var textoCelda = document.createTextNode("Product");
				}else if (j==1) {
					var textoCelda = document.createTextNode("Quantity");
				}else if(j ==2){
					var textoCelda = document.createTextNode("Price");
				}else{
					var textoCelda = document.createTextNode("Order Number");
				}
			}else{
				if(j==0){
					var textoCelda = document.createTextNode(initialOrdObj["products"][i-1].product);
				}else if(j==1){
					var textoCelda = document.createTextNode(initialOrdObj["products"][i-1].quantity);
				}else if(j==2){
					var textoCelda = document.createTextNode(initialOrdObj["products"][i-1].price);
				}else{
					var textoCelda = document.createTextNode(initialOrdObj["order_id"]);
				}
			}
			celda.appendChild(textoCelda);
			hilera.appendChild(celda);
    }

    tblBody.appendChild(hilera);
  }
  tabla.appendChild(tblBody);
  body.appendChild(tabla);
  tabla.setAttribute("border", "2");
	tabla.setAttribute("id", initialOrdObj["order_id"]);
	var espacio   = document.createElement("br");
	body.appendChild(espacio);
}

function generarTablaPorOrden(newOrdObj) {
  var body = document.getElementsByTagName("main")[0];
  var tabla   = document.createElement("table");
  var tblBody = document.createElement("tbody");
  for (var i = 0; i <= newOrdObj["products"].length ; i++) {
    var hilera = document.createElement("tr");
    for (var j = 0; j < 4; j++) {
      var celda = document.createElement("td");
			if(i == 0){
				if(j==0){
					var textoCelda = document.createTextNode("Product");
				}else if (j==1) {
					var textoCelda = document.createTextNode("Quantity");
				}else if(j ==2){
					var textoCelda = document.createTextNode("Price");
				}else{
					var textoCelda = document.createTextNode("Order Number");
				}
			}else{
				if(j==0){
					var textoCelda = document.createTextNode(newOrdObj["products"][i-1].product);
				}else if(j==1){
					var textoCelda = document.createTextNode(newOrdObj["products"][i-1].quantity);
				}else if(j==2){
					var textoCelda = document.createTextNode(newOrdObj["products"][i-1].price);
				}else{
					var textoCelda = document.createTextNode(newOrdObj["order_id"]);
				}
			}
			celda.appendChild(textoCelda);
			hilera.appendChild(celda);
    }

    tblBody.appendChild(hilera);
  }
  tabla.appendChild(tblBody);
  body.appendChild(tabla);
  tabla.setAttribute("border", "2");
	tabla.setAttribute("id", newOrdObj["order_id"]);
	var espacio   = document.createElement("br");
	body.appendChild(espacio);
}

function agregarNuevaOrden(){               
	generarTablaPorOrden(newOrdObj);

	
}

function removeOrderById(id){	
	document.getElementById(id).innerHTML = "";
}
function loadOrders(){
	generarTablaPorOrden(initialOrdObj);
	axios.get("/orders")
	.then(function (response){
		var orders = response.data;
		for(var i=0;i<orders.length;i++){
				addNewOrder(orders[i]);
		}
	})
	.catch(function (error){
		console.log("There is a problem with our servers. We apologize for the inconvince, please try again later");
	});

}

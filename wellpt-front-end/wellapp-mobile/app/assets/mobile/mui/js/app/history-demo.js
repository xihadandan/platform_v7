window.addEventListener('hashchange', function(event) {
	console.log("hash", location.hash);
}, false);
console.log("---hash", location.hash);
location.hash="A1";
console.log("---hash", location.hash);
location.hash="A2";
console.log("---hash", location.hash);
location.hash="A3";
console.log("---hash", location.hash);
history.back();
console.log("---hash", location.hash);
history.back();
console.log("---hash", location.hash);
location.hash="B2";
console.log("---hash", location.hash);
history.back();



window.addEventListener('popstate', function(event) {
	console.log("state", event.state);
}, false);
console.log("---state", history.state);
history.pushState({panel:"A1"},"");
console.log("---state", history.state);
history.pushState({panel:"A2"},"");
console.log("---state", history.state);
history.pushState({panel:"A3"},"")
console.log("---state", history.state);
history.back();
console.log("---state", history.state);
history.back();
console.log("---state", history.state);
history.pushState({panel:"B2"},"");
console.log("---state", history.state);
history.back();
console.log("---state", history.state);
history.back();
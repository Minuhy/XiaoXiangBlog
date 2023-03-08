function sikpNewPage(u){
	var skipUrl;
	if(u.endsWith("login.jsp")){
		skipUrl = u+'?u='+window.location.href+'&n='+document.title;
	}else{
		skipUrl = u;
	}
	console.log("link: " + skipUrl);
	setTimeout(function(){
		location.href=skipUrl;
	},2000);
}
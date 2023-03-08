<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%String currentPath = request.getContextPath();%>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<!-- 上述3个meta标签*必须*放在最前面，任何其他内容都*必须*跟随其后！ -->
<link rel="shortcut icon" href="<%= currentPath %>/img/icon.svg">
<style type="text/css">
	body { overflow-y: scroll; }
	table,td{
		margin: auto;
		border: 1px solid #b11630;
		/*border-style:solid dashed  double dashed; */
	}
	td{
		padding: 6px;
	}

    .nav {
    	min-width:1500px;
        text-align:left;
        font-size: 28px;
        color: #fff;
        border-radius: 20px;
        padding: 10px 30px;
        background-image: linear-gradient(to right,rgb(180, 21, 21),rgb(166, 27, 179));
    }
    .nav a{
        color: #fff;
        text-decoration: none;
        padding: 10px;
        border: 5px solid #00000000;
    }
    .nav a:hover{
        border: 5px solid #000;
        background-color: #fff;
        color: #000;
    }
    #page-nav{
        padding: 20px;
    }
    #page-nav a{
        color: #000;
        text-decoration: none;
        padding: 10px;
        border: 3px solid #00000000;
        margin: 10px;
        background-color: #46b8da;
        border-radius: 5px;
    }
    #page-nav a:hover{
        border: 3px solid #e611bb;
        background-color: #000;
        color: #fff;
    }
    #page-nav form{
        margin: 10px;
    }
    #statistics{
        padding: 20px;
        text-align: center;
    }
    
	#indexTable td{
	    padding: 20px;
	    border: 2px double #be4789b6;
	}
	
	#indexTable{
	    border: 5px double #be4789b6;
	}
	body{
		text-align: center;
	}
	button{
		font-size: 16px;
		padding: 6px;
	}
	
	.can-search{
		color:#ff5e00;
	}
	
</style>
<?php
   $con = mysqli_connect("localhost", "root", "","Patient");

    mysqli_set_charset($con, "utf8");
    $p_id = $_POST["p_id"];
    $walk = $_POST["walk"];
    $datetime = $_POST["datetime"];

    $statement = "INSERT INTO distance (p_id, walk, datetime) VALUES ('$p_id', '$walk', '$datetime')";
     
    if(mysqli_query($con, $statement)){
	echo '입력';
    } else{
	echo '입력 실패';
    }
?>
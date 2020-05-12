<?php
   $con = mysqli_connect("localhost", "root", "","Patient");

    mysqli_set_charset($con, "utf8");
    $p_id = $_POST["p_id"];
    $walk = $_POST["walk"];

    $statement = "INSERT INTO distance (p_id, walk) VALUES ('$p_id', '$walk')";
     
    if(mysqli_query($con, $statement)){
	echo '입력';
    } else{
	echo '입력 실패';
    }
?>
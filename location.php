<?php
   $con = mysqli_connect("localhost", "root", "","Patient");

    mysqli_set_charset($con, "utf8");
    $p_id = $_POST["p_id"];
    $p_latitude = $_POST["p_latitude"];
    $p_longitude = $_POST["p_longitude"];

    $statement = "UPDATE location SET p_latitude = '$p_latitude', p_longitude = '$p_longitude' WHERE p_id = $p_id";
     
    if(mysqli_query($con, $statement)){
	echo '업로드';
    } else{
	echo '업로드 실패';
    }
?>
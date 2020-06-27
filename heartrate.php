<?php
   $con = mysqli_connect("localhost", "root", "","Patient");

    mysqli_set_charset($con, "utf8");
    $p_id = $_POST["p_id"];
    $h_heartrate = $_POST["h_heart"];

    $statement = "UPDATE Patient SET h_heartrate = '$h_heart' WHERE p_id = $p_id";
     
    if(mysqli_query($con, $statement)){
	echo '업로드';
    } else{
	echo '업로드 실패';
    }
?>
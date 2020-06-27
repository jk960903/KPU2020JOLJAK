<?php
   $con = mysqli_connect("localhost", "root", "","Patient");

    mysqli_set_charset($con, "utf8");
    $p_id = $_POST["p_id"];
    $p_pw = $_POST["p_pw"];

    $statement = mysqli_prepare($con, "SELECT p_id, p_pw FROM Patient WHERE p_id = ? AND p_pw = ?");
     mysqli_stmt_bind_param($statement, 'ss', $p_id, $p_pw);
     mysqli_stmt_execute($statement);

     mysqli_stmt_store_result($statement);
     mysqli_stmt_bind_result($statement, $p_id, $p_pw);

    $response = array();
    $response["success"] = false;

    while(mysqli_stmt_fetch($statement)){
     $response["success"] = true;
     $response["p_id"] = $p_id;
$response["p_pw"] = $p_pw;
     }
    echo json_encode($response);
?>
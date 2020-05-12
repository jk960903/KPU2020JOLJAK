<?php

    $con = mysqli_connect('localhost', 'root', '', 'Patient');
    mysqli_set_charset($con, "utf8");

     $p_id = $_POST['p_id'];
     $p_pw = $_POST['p_pw'];
     $p_name =  $_POST['p_name'];
     $p_phone = $_POST['p_phone'];
     $p_birth = $_POST['p_birth'];
     //insert 쿼리문을 실행함
     $statement = mysqli_prepare($con, "INSERT INTO Patient (p_id, p_pw, p_name, p_phone, p_birth) VALUES (?, ?, ?, ?, ?)");
     mysqli_stmt_bind_param($statement, "ssssi", $p_id, $p_pw, $p_name, $p_phone, $p_birth);
     mysqli_stmt_execute($statement);
     mysqli_stmt_store_result($statement);

     $response = array();
     $response["success"] = true;
     //회원 가입 성공을 알려주기 위한 부분임
     echo json_encode($response); 
?>
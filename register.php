<?php

    $con = mysqli_connect('localhost', 'root', '', 'Patient');
    mysqli_set_charset($con, "utf8");

     $p_id = $_POST['p_id'];
     $p_pw = $_POST['p_pw'];
     $p_name =  $_POST['p_name'];
     $p_phone = $_POST['p_phone'];
     $p_birth = $_POST['p_birth'];
     //insert �������� ������
     $statement = mysqli_prepare($con, "INSERT INTO Patient (p_id, p_pw, p_name, p_phone, p_birth) VALUES (?, ?, ?, ?, ?)");
     mysqli_stmt_bind_param($statement, "ssssi", $p_id, $p_pw, $p_name, $p_phone, $p_birth);
     mysqli_stmt_execute($statement);
     mysqli_stmt_store_result($statement);

     $response = array();
     $response["success"] = true;
     //ȸ�� ���� ������ �˷��ֱ� ���� �κ���
     echo json_encode($response); 
?>
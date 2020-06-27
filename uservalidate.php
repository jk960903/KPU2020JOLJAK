<?php

    $con = mysqli_connect('localhost', 'root', '', 'Patient');
     $p_id = $_POST["p_id"];
mysqli_set_charset($con, "utf8");
     $statement = mysqli_prepare($con, "SELECT p_id FROM Patient WHERE p_id = ?");

     mysqli_stmt_bind_param($statement, 's', $p_id);
     mysqli_stmt_execute($statement);

     mysqli_stmt_store_result($statement);
     mysqli_stmt_bind_result($statement, $p_id);

     $response = array();
     $response["success"] = true;

     while(mysqli_stmt_fetch($statement)){
       $response["success"] = false;//ȸ�����ԺҰ��� ��Ÿ��
       $response["p_id"] = $p_id;
     }
     //�����ͺ��̽� �۾��� ���� Ȥ�� �����Ѱ��� �˷���
     echo json_encode($response);
?>
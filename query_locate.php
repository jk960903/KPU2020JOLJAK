<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 

include('dbcon.php');

   

//POST 값을 읽어온다.
$locate=isset($_POST['locate']) ? $_POST['locate'] : '';
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


if ($locate != "" ){ 

    $sql="select * from location where p_longitude";
    $stmt = $con->prepare($sql);
    $stmt->execute();
 
    if ($stmt->rowCount() == 0){

        echo "'";
        echo $p_latitude;
        echo "'은 찾을 수 없습니다.";
    }
	else{

   		$data = array(); 

        while($row=$stmt->fetch(PDO::FETCH_ASSOC)){

        	extract($row);

            array_push($data, 
                  array('p_name'=>$row["p_name"],
                'p_longitude'=>$row["p_longitude"],
                'p_latitude'=>$row["p_latitude"]
            ));
        }


        if (!$android) {
            echo "<pre>"; 
            print_r($data); 
            echo '</pre>';
        }else
        {
            header('Content-Type: application/json; charset=utf8');
            $json = json_encode(array("root"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
            echo $json;
        }
    }
}
else {
    echo "위치 ";
}

?>



<?php

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if (!$android){
?>

<html>
   <body>
   
      <form action="<?php $_PHP_SELF ?>" method="POST">
         위치: <input type = "text" name = "country" />
         <input type = "submit" />
      </form>
   
   </body>
</html>
<?php
}

   
?>
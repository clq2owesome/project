<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Swiper demo</title>
    <!-- Link Swiper's CSS -->
    <link rel="stylesheet" href="swiper/animate.min.css">
    <link rel="stylesheet" href="swiper/swiper-3.2.7.min.css">

    <!-- Demo styles -->
    <style>
    body {
        background: #eee;
        font-family: Helvetica Neue, Helvetica, Arial, sans-serif;
        font-size: 14px;
        color:#000;
        margin: 0;
        padding: 0;
    }
    .swiper-container {
        width: 300px;
        height: 500px;
        margin: 20px auto;
    }
    .swiper-slide {
        text-align: center;
        font-size: 18px;
        background: #fff;
        
        /* Center slide text vertically */
        display: -webkit-box;
        display: -ms-flexbox;
        display: -webkit-flex;
        display: flex;
        -webkit-box-pack: center;
        -ms-flex-pack: center;
        -webkit-justify-content: center;
        justify-content: center;
        -webkit-box-align: center;
        -ms-flex-align: center;
        -webkit-align-items: center;
        align-items: center;
    }
    </style>
</head>
<body>
    <!-- Swiper -->
    <div class="swiper-container">
        <div class="swiper-wrapper">
        	<div class="swiper-slide">
	            <div class="ani" style="background-image:url(swiper/IMG_0392.JPG);width:300px;height:500px;background-size:100%;"
	                 swiper-animate-effect="zoomInDown" swiper-animate-duration="1s" swiper-animate-delay="0.3s" >
	            	<p class="ani" swiper-animate-effect="zoomInDown" swiper-animate-duration="1s" swiper-animate-delay="1.3s" style="color:red;">这是内容1</p>
	            </div>
            </div>
            <div class="swiper-slide" style="background-image:url(swiper/IMG_0411.JPG);width:300px;height:500px;background-size:100%;">
            	<p class="ani" swiper-animate-effect="zoomInDown" swiper-animate-duration="1s" swiper-animate-delay="0.3s" style="color:red;">这是内容2</p>
            </div>
            
            <<div class="swiper-slide" style="background-image:url(swiper/IMG_0412.JPG);width:300px;height:500px;background-size:100%;">
            	<p class="ani" swiper-animate-effect="zoomInDown" swiper-animate-duration="1s" swiper-animate-delay="0.3s" style="color:red;">这是内容3</p>
            </div>
            
            <div class="swiper-slide" style="background-image:url(swiper/IMG_0413.JPG);width:300px;height:500px;background-size:100%;">
            	<p class="ani" swiper-animate-effect="zoomInDown" swiper-animate-duration="1s" swiper-animate-delay="0.3s" style="color:red;">这是内容4</p>
            </div>
            
            <div class="swiper-slide" style="background-image:url(swiper/IMG_0414.JPG);width:300px;height:500px;background-size:100%;">
            	<p class="ani" swiper-animate-effect="zoomInDown" swiper-animate-duration="1s" swiper-animate-delay="0.3s" style="color:red;">这是内容5</p>
            </div>
            
            <div class="swiper-slide" style="background-image:url(swiper/IMG_0415.JPG);width:300px;height:500px;background-size:100%;">
            	<p class="ani" swiper-animate-effect="zoomInDown" swiper-animate-duration="1s" swiper-animate-delay="0.3s" style="color:red;">这是内容6</p>
            </div>
            
            <div class="swiper-slide" style="background-image:url(swiper/IMG_0426.JPG);width:300px;height:500px;background-size:100%;">
            	<p class="ani" swiper-animate-effect="zoomInDown" swiper-animate-duration="1s" swiper-animate-delay="0.3s" style="color:red;">这是内容7</p>
            </div>
            
            <div class="swiper-slide" style="background-image:url(swiper/IMG_0705.PNG);width:300px;height:500px;background-size:100%;">
            	<p class="ani" swiper-animate-effect="zoomInDown" swiper-animate-duration="1s" swiper-animate-delay="0.3s" style="color:red;">这是内容8</p>
            </div>
            
        </div>
    </div>

    <!-- Swiper JS -->
    <script src="swiper/swiper-3.2.7.min.js"></script>
    <script src="swiper/swiper.animate1.0.2.min.js"></script>

    <!-- Initialize Swiper -->
    <script>
    var swiper = new Swiper('.swiper-container', {
    	direction : 'vertical',
    	effect : 'cube',
    	cube: {
    	  slideShadows: true,
    	  shadow: true,
    	  shadowOffset: 100,
    	  shadowScale: 0.6
    	},
    	onInit: function(swiper){ //Swiper2.x的初始化是onFirstInit
  		   swiperAnimateCache(swiper); //隐藏动画元素 
  		   swiperAnimate(swiper); //初始化完成开始动画
  		}, 
  		onSlideChangeEnd: function(swiper){ 
  		  swiperAnimate(swiper); //每个slide切换结束时也运行当前slide动画
  		} 
    });
    </script>
</body>
</html>
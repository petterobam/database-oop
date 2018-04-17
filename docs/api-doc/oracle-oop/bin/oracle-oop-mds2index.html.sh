#!/bin/bash
# author:petterobam
# url:https://github.com/petterobam/my-mds2index.html
# Usage: sh my-mds2indexhtml.sh [渲染配置文件路径，默认 ../config/config.yml] [MarkDown文件夹路径，默认 ../markdowns] [index.html模板文件路径，默认 ../template/index.html] [输出文件路径，默认 ../output/index.html]
echo "#############################################################"
echo ">>>>>>>>>>>接收准备参数"
curr_path="`cd .. && pwd`"
echo ">>>>>>>>>>>默认文件所在文件夹：${curr_path}"
# 检查渲染配置文件默认路径
conf_path="$curr_path/config/config.yml"
# MarkDown文件夹默认路径
mds_path="$curr_path/markdowns"
# index.html模板文件默认路径
tpl_path="$curr_path/template/index.html"
# 输出文件默认路径
out_path="$curr_path/index.html"
echo "#############################################################"
echo ">>>>>>>>>>>启动配置读取..."
echo "--------------------------"
echo ">>>>>>>>>>>渲染配置文件路径：$conf_path"
echo "--------------------------"
echo ">>>>>>>>>>>MarkDown文件夹路径：$mds_path"
echo "--------------------------"
echo ">>>>>>>>>>>index.html模板文件路径：$tpl_path"
echo "--------------------------"
echo ">>>>>>>>>>>输出文件路径：$out_path"
echo "--------------------------"
echo ">>>>>>>>>>>配置读取完毕！！"
echo "#############################################################"
jar_path="`cd .. && pwd`/jar-util/my-mds2index.html-exe-v1.0.0.jar"
echo ">>>>>>>>>>>用于将MarkDown转化单索引页面的jar包路径：${jar_path}"
echo "------------------------------------------------------------"
echo ">>>>>>>>>>>开始转化..."
echo "#############################################################"
echo "执行：java -jar $jar_path $conf_path $mds_path $tpl_path $out_path > ./oracle-oop-mds2index.html.out < /dev/null &"
java -jar $jar_path $conf_path $mds_path $tpl_path $out_path > ./oracle-oop/log/oracle-oop-mds2index.html.out < /dev/null &
echo "#############################################################"
echo ">>>>>>>>>>>转化结束！！"
echo "------------------------------------------------------------"
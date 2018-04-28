@Echo Off
:: author:petterobam
:: url:https://github.com/petterobam/my-mds2index.html
@Title my-mds2indexhtml@petterobam
Rem CenterSelf
:config_load
echo "#############################################################"
echo ">>>>>>>>>>>config"
:: bat current Path
Set curr_path=%~dp0
echo ">>>>>>>>>>>curr_path %curr_path%"
:: remove elasticsearch-oop/bin/
set jar_base_path=%curr_path:~0,-22%
:: jar File Path
set jar_path=%jar_base_path%jar-util\my-mds2index.html-exe-v1.0.0.jar
:: remove bin/
Set resource_path=%curr_path:~0,-4%
:: replace \ to /
Set resource_path=/%resource_path:\=/%
:: Config File Path
set conf_path=%resource_path%config/config.yml
:: MarkDown Files Floder Path
set mds_path=%resource_path%markdowns
:: index.html Tpl File Path
set tpl_path=%resource_path%template/index.html
:: Output File Path
set out_path=%resource_path%index.html
echo "#############################################################"
echo ">>>>>>>>>>>fill config..."
echo "--------------------------"
echo ">>>>>>>>>>>Config File Path:%conf_path%"
echo "--------------------------"
echo ">>>>>>>>>>>MarkDown Files Floder Path:%mds_path%"
echo "--------------------------"
echo ">>>>>>>>>>>index.html Tpl File Path:%tpl_path%"
echo "--------------------------"
echo ">>>>>>>>>>>Output File Path:%out_path%"
echo "--------------------------"
echo ">>>>>>>>>>>finish config!!"
echo "#############################################################"
echo ">>>>>>>>>>>mds2index.html jar File Path:%jar_path%"
echo "------------------------------------------------------------"
:mds2indexhtml
echo ">>>>>>>>>>>starting convert..."
echo "#############################################################"
Set log_base_path=%curr_path:~0,-4%
set log_path=%log_base_path%log\elasticsearch-oop-mds2index.html.log
set error_log_path=%log_base_path%log\elasticsearch-oop-mds2index.html-error.log
echo "--------------------------"
echo ">>>>>>>>>>>Log File Path:%log_path%"
echo "--------------------------"
echo ">>>>>>>>>>>Error Log File Path:%error_log_path%"
echo "#############################################################"
echo "run java -jar %jar_path% %conf_path% %mds_path% %tpl_path% %out_path% >> %log_path% 2>> %error_log_path%"
java -jar %jar_path% %conf_path% %mds_path% %tpl_path% %out_path% >> %log_path% 2>> %error_log_path%
echo "#############################################################"
echo ">>>>>>>>>>>convert over!!"
echo "------------------------------------------------------------"
Pause
Exist
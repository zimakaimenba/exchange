param([string]$step)


# PowerShell代码
# 添加 System.speech.dll 引用
Add-Type -AssemblyName System.speech

$PID
Write-Output $PID
# 创建 SpeechSynthesizer 对象

$syn=New-Object System.Speech.Synthesis.SpeechSynthesizer

#设置朗读的音量
$syn.Volume=80

#设置朗读的语速
$syn.Rate=2
$syn.Speak($step)


 

 


param([string]$step)


# PowerShell����
# ��� System.speech.dll ����
Add-Type -AssemblyName System.speech

$PID
Write-Output $PID
# ���� SpeechSynthesizer ����

$syn=New-Object System.Speech.Synthesis.SpeechSynthesizer

#�����ʶ�������
$syn.Volume=80

#�����ʶ�������
$syn.Rate=2
$syn.Speak($step)


 

 


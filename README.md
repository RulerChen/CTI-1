# CTI Report - 1

[Chinese Threat Actor Used Modified Cobalt Strike Variant to Attack Taiwanese Critical Infrastructure](https://blog.eclecticiq.com/chinese-threat-actor-used-modified-cobalt-strike-variant-to-attack-taiwanese-critical-infrastructure) 

由於我們無法取得原始的 Cobalt Strike Cat，因此我們使用文中提到的 log4j 漏洞來取代 Cobalt Strike Cat 的功能。


主要包括了以下幾個部分：

1. Step1 : 使用 OneForAll 列出子網、Nuclei 掃描 log4j 漏洞
2. Step2 + 3 : 使用 log4j 漏洞並回傳具有權限的 reverse shell
3. Step4 : 使用 Nmap 跟 fscan 掃描內部網路
4. Step5 : 使用 LaZagne、Minikatz 跟 HackBrowserData 偷取資料、並利用 BloodHound 分析權限
5. Step6 : 實作 Windows Service 來建立持久連線
6. Step7 : 使用 frp 來把服務映射到外網

可以參考以下影片跟網站 :

- [OneForAll 跟 Nuclei 操作影片](https://www.youtube.com/watch?v=k0nTI4TkkQw)
- [log4j 實作網站](https://rulerchen.github.io/RulerChen-Website/docs/Security/log4j/)
- [log4j 與 windows service 操作影片](https://www.youtube.com/watch?v=kj2v_CQ8sSM)
- [FRP 操作影片](https://www.youtube.com/watch?v=UjRJ2_PlR1M)
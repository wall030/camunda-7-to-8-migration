import { Client, logger, Variables } from "camunda-external-task-client-js";

const config = {
    baseUrl: "http://localhost:8080/engine-rest",
    use: logger,
};

const client = new Client(config);

client.subscribe("generateQrCode", async function ({ task, taskService }) {
    try {
        const summary = "Exam for " + task.variables.get("course");
        const location = "Hochschule Kaiserslautern, 66482 Zweibrücken, Germany";
        const description = summary;
        const website = "https://hs-kl.de";
        const repeatRule = "";
        const reminder = "-PT24H";

        // Set start and end date 1 month from now)
        const now = new Date();
        now.setMonth(now.getMonth() + 1);

        const yyyy = now.getFullYear();
        const mm = String(now.getMonth() + 1).padStart(2, "0");
        const dd = String(now.getDate()).padStart(2, "0");

        const startDate = `${yyyy}${mm}${dd}T080000`;
        const endDate = `${yyyy}${mm}${dd}T100000`;

        const qrCodeUrl = `https://qrickit.com/api/qr.php?d=BEGIN%3AVEVENT%0D%0ASUMMARY%3A${encodeURIComponent(summary)}%0D%0ALOCATION%3A${encodeURIComponent(location)}%0D%0AURL%3A${encodeURIComponent(website)}%0D%0ADESCRIPTION%3A${encodeURIComponent(description)}%0D%0ADTSTART%3A${encodeURIComponent(startDate)}%0D%0ADTEND%3A${encodeURIComponent(endDate)}%0D%0ARRULE%3A${encodeURIComponent(repeatRule)}%0D%0ABEGIN%3AVALARM%0D%0ATRIGGER%3A${encodeURIComponent(reminder)}%0D%0AACTION%3ADISPLAY%0D%0AEND%3AVALARM%0D%0AEND%3AVEVENT%0D%0A&t=g&addtext=&txtcolor=000000&fgdcolor=000000&bgdcolor=FFFFFF&qrsize=200`;
        
        console.log("Generated QR Code URL:", qrCodeUrl);
 
        const qrCode = new Variables().set("qrCodeUrl", qrCodeUrl);

        await taskService.complete(task, qrCode);

        console.log(`Task ${task.id} completed! QR Code URL: ${qrCodeUrl}`);
    } catch (error) {
        console.error("Error processing task:", error.message);
        await taskService.handleFailure(task, error.message, error.stack, 0, 5000);
    }
});

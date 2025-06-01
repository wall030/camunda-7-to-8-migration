import { Camunda8 } from "@camunda8/sdk";

const c8 = new Camunda8({
           	ZEEBE_ADDRESS: process.env.ZEEBE_ADDRESS,
           	ZEEBE_REST_ADDRESS: process.env.ZEEBE_REST_ADDRESS,
           	ZEEBE_CLIENT_ID: process.env.ZEEBE_CLIENT_ID,
           	ZEEBE_CLIENT_SECRET: process.env.ZEEBE_CLIENT_SECRET,
           	CAMUNDA_OAUTH_STRATEGY: process.env.CAMUNDA_OAUTH_STRATEGY,
           	CAMUNDA_OAUTH_URL: process.env.CAMUNDA_OAUTH_URL
           })

const restClient = c8.getCamundaRestClient(); // New REST API

const worker = restClient.createJobWorker({
  type: 'generateQrCode',
  worker: 'qr-code-worker',
  timeout: 30000,
  maxJobsToActivate: 10,
  pollIntervalMs: 5000,
  autoStart: true,
  requestTimeout: 10000,
  jobHandler: async (job, log) => {
    try {
      const summary = "Exam for " + job.variables.course;
      const location = "Hochschule Kaiserslautern, 66482 Zweibrücken, Germany";
      const description = summary;
      const website = "https://hs-kl.de";
      const repeatRule = "";
      const reminder = "-PT24H";

      const now = new Date();
      now.setMonth(now.getMonth() + 1);

      const yyyy = now.getFullYear();
      const mm = String(now.getMonth() + 1).padStart(2, "0");
      const dd = String(now.getDate()).padStart(2, "0");

      const startDate = `${yyyy}${mm}${dd}T080000`;
      const endDate = `${yyyy}${mm}${dd}T100000`;

      const qrCodeUrl = `https://qrickit.com/api/qr.php?d=BEGIN%3AVEVENT%0D%0ASUMMARY%3A${encodeURIComponent(summary)}%0D%0ALOCATION%3A${encodeURIComponent(location)}%0D%0AURL%3A${encodeURIComponent(website)}%0D%0ADESCRIPTION%3A${encodeURIComponent(description)}%0D%0ADTSTART%3A${encodeURIComponent(startDate)}%0D%0ADTEND%3A${encodeURIComponent(endDate)}%0D%0ARRULE%3A${encodeURIComponent(repeatRule)}%0D%0ABEGIN%3AVALARM%0D%0ATRIGGER%3A${encodeURIComponent(reminder)}%0D%0AACTION%3ADISPLAY%0D%0AEND%3AVALARM%0D%0AEND%3AVEVENT%0D%0A&t=g&addtext=&txtcolor=000000&fgdcolor=000000&bgdcolor=FFFFFF&qrsize=200`;

      log.info(`Generated QR Code URL: ${qrCodeUrl}`);


      return job.complete({ qrCodeUrl });

      log.info(`Task ${job.key} completed! QR Code URL: ${qrCodeUrl}`);
    } catch (error) {
      log.error(`Error processing task: ${error.message}`);

      return job.fail(error.message);
    }
  }
});

console.log(`Job worker started for task type: generateQrCode`);
<h1>Cyber Security Project</h1>

<h2>Setup Instructions</h2>

<h3>1. Clone the Repository</h3>
<pre><code>git clone &lt;repository_url&gt;
cd &lt;project_directory&gt;
</code></pre>

<h3>2. Firebase Setup</h3>
<ol>
    <li>Log in to your <a href="https://console.firebase.google.com/" target="_blank">Firebase Console</a>.</li>
    <li>Create a new project or select an existing one.</li>
    <li>Download the <code>google-services.json</code> file from your Firebase project.</li>
    <li>Place the <code>google-services.json</code> file in the <code>/app</code> directory of your project.</li>
</ol>

<h3>3. SendGrid Setup</h3>
<ol>
    <li>Log in to your <a href="https://sendgrid.com/" target="_blank">SendGrid Account</a>.</li>
    <li>Generate an API Key:
        <ol>
            <li>Go to <b>Settings > API Keys</b>.</li>
            <li>Click on <b>Create API Key</b>.</li>
            <li>Note the generated API key.</li>
        </ol>
    </li>
    <li>In your SendGrid account, configure your email address (sender email) under <b>Sender Authentication</b>.</li>
</ol>

<h3>4. Configure Local Properties</h3>
<ol>
    <li>Open the <code>local.properties</code> file located in the root of your project.</li>
    <li>Add the following entries:
        <pre><code>SENDGRID_API_KEY=&lt;your_sendgrid_api_key&gt;
FROM_EMAILL=&lt;your_sendgrid_sender_email&gt;
</code></pre>
    </li>
    <li>Save the file.</li>
</ol>

<h3>5. Build and Run the Project</h3>
<ol>
    <li>Open the project in Android Studio.</li>
    <li>Sync the Gradle files by clicking <b>Sync Now</b> if prompted.</li>
    <li>Connect a physical device or an emulator.</li>
    <li>Build and run the project.</li>
</ol>

<hr>

<h2>Troubleshooting</h2>
<ul>
    <li><b>Firebase Authentication Issues:</b> Ensure the SHA-1 fingerprint of your app is registered in the Firebase Console.</li>
    <li><b>Email Sending Errors:</b> Double-check your SendGrid API key and sender email in <code>local.properties</code>.</li>
</ul>

<hr>

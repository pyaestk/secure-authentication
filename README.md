# Security School Project

This Android app was developed with a focus on modern cybersecurity principles and robust user authentication, implemented using **MVVM architecture**, **Kotlin**, **Firebase**, and **HCaptcha**. Below are the key security features included:

### 🛡️ Password Management
- **Strong Password Rules**: Enforces minimum 9 characters with uppercase, lowercase, 2 digits, and 1 special character.
- **Password Strength Meter**: Dynamically evaluates strength (`Weak`, `Medium`, `Strong`, `Perfect`) as users type.
- **Password Confirmation**: Ensures password re-entry during registration to avoid typos.
- **Secure Reset Functionality**: Validates current password before allowing a reset.
- **Encryption**: Uses Firebase Authentication with bcrypt hashing for secure password storage.

### 🤖 CAPTCHA Integration (HCaptcha)
- **Privacy-Focused CAPTCHA**: Integrated HCaptcha to protect against bots without tracking users.
- **Challenge-Based Security**: Enforces CAPTCHA after multiple failed login attempts and during registration.
- **Bot Resistance**: Advanced, image-based challenges that evolve in real-time to resist automation.

### 📧 Additional Security Features
- **Email Verification**: Confirms email ownership post-registration.
- **Login Timeout**: Temporarily disables login and notifies users after repeated failures.
- **OTP-Based Two Factor Authentication**: Sends one-time passwords via email to secure user sessions.

These features collectively enhance the security posture of the app and offer protection against common threats such as brute-force attacks, automated bots, and fake account creation.

## 📸 Screenshots

<img src="https://github.com/user-attachments/assets/62305748-ed40-4788-b558-3242f1db4fd8" width="200" />
<img src="https://github.com/user-attachments/assets/caa69f38-9901-4484-98f3-117781da2f64" width="200" />
<img src="https://github.com/user-attachments/assets/ddeb0181-2378-4b2f-8bdb-23332d49daf6" width="200" />

<img src="https://github.com/user-attachments/assets/5bdf6504-e1d4-4f18-9a86-c2b5b519f05e" width="200" />
<img src="https://github.com/user-attachments/assets/03c04fec-ea5e-4df8-8084-4bc525af39a0" width="200" />
<img src="https://github.com/user-attachments/assets/ddb45100-c96f-4d70-9c8c-060dfa97efd4" width="200" />
<img src="https://github.com/user-attachments/assets/d6916db0-96e0-4615-ae25-e0ab0eb59281" width="200" />
<img src="https://github.com/user-attachments/assets/c6cb0c8a-0fa4-4269-9e71-1a7a69121431" width="200" />

<img src="https://github.com/user-attachments/assets/867159b1-21b5-4069-a25f-74470ce54e56" width="200" />

<img src="https://github.com/user-attachments/assets/d2e05ced-70b0-4f6b-9455-fab3b86bf16e" width="200" />
<img src="https://github.com/user-attachments/assets/f08ae9e2-6a3e-4243-b41c-111b8d1b8ebc" width="200" />
<img src="https://github.com/user-attachments/assets/420ca687-dba1-4ddf-bd61-e7f2346af56a" width="200" />
<img src="https://github.com/user-attachments/assets/daf97185-d40e-4975-98c1-aa1f30c7b50d" width="200" />


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

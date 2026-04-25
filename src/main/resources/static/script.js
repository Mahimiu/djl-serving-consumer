function checkFiles(files) {
    console.log(files);

    if (files.length != 1) {
        alert("Bitte genau eine Datei hochladen.");
        return;
    }

    const fileSize = files[0].size / 1024 / 1024; // in MiB
    if (fileSize > 10) {
        alert("Datei zu gross (max. 10MB)");
        return;
    }

    // Show loading, hide results
    document.getElementById('loadingArea').style.display = 'block';
    document.getElementById('answerPart').style.display = 'none';

    const file = files[0];

    // Preview
    if (file) {
        preview.src = URL.createObjectURL(files[0]);
    }

    // Upload
    const formData = new FormData();
    for (const name in files) {
        formData.append("image", files[name]);
    }

    fetch('/analyze', {
        method: 'POST',
        body: formData
    }).then(response => {
        return response.json();
    }).then(data => {
        console.log('Received data:', data);
        
        // Hide loading
        document.getElementById('loadingArea').style.display = 'none';
        document.getElementById('answerPart').style.display = 'block';
        
        // Sort by probability (descending) and take top 5
        const sortedData = data.sort((a, b) => b.probability - a.probability).slice(0, 5);
        
        // Render results using HTML template to reduce JS-generated markup
        const answerEl = document.getElementById('answer');
        const tpl = document.getElementById('result-item-template');
        answerEl.innerHTML = '';
        
        sortedData.forEach((item) => {
            const percentage = (item.probability * 100).toFixed(2);
            const progressWidth = percentage;
            const displayName = (item.className || '')
                .replace(/^[a-z]\d{8}[ ,]*/i, '')
                .trim();

            const node = tpl.content.firstElementChild.cloneNode(true);
            const bar = node.querySelector('.progress-bar');
            const label = node.querySelector('.bar-label');

            bar.style.width = `${progressWidth}%`;
            bar.setAttribute('aria-valuenow', progressWidth);
            label.textContent = `${displayName} · ${percentage}%`;
            label.title = `${displayName} · ${percentage}%`;

            answerEl.appendChild(node);
        });
    }).catch(error => {
        console.log(error);
        document.getElementById('loadingArea').style.display = 'none';
        document.getElementById('answerPart').style.display = 'block';
        document.getElementById('answer').innerHTML = 
            '<div class="alert alert-danger alert-custom" role="alert">❌ Fehler beim Analysieren des Bildes. Bitte versuchen Sie es erneut.</div>';
    });
}
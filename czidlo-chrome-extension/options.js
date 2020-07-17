function save_options() {
    var instance = document.getElementById('instance').value;
    chrome.storage.sync.set({
        instance: instance,
    }, function (data) {
        var saved = document.getElementById('saved');
        var save = document.getElementById('save');
        saved.style.display = "block";
        save.style.display = "none";
        setTimeout(function () {
            saved.style.display = "none";
            save.style.display = "block";
        }, 750);
    });
}

function restore_options() {
    chrome.storage.sync.get({
        instance: 'resolver.nkp.cz'
    }, function (items) {
        document.getElementById('instance').value = items.instance;
    });
}
document.addEventListener('DOMContentLoaded', restore_options);
document.getElementById('save').addEventListener('click', save_options);
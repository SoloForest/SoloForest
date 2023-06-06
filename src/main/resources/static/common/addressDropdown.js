$(document).ready(function () {
    const doDropdown = $("#do");
    const siDropdown = $("#si");

    function populateDoDropdown() {
        $.getJSON("/common/json/do-si.json")
            .done((data) => {
                data.forEach((entry) => {
                    doDropdown.append(
                        $("<option>", {value: entry.do, text: entry.do})
                    );
                });
            })
            .fail((error) => {
                console.error("Error fetching JSON data:", error);
            });
    }

    function updateSiDropdown(selectedDo) {
        siDropdown.empty();
        siDropdown.append($("<option>", {value: "", text: "시 선택"}));

        if (selectedDo) {
            $.getJSON("/common/json/do-si.json")
                .done((data) => {
                    const selectedDoData = data.find((entry) => entry.do === selectedDo);
                    selectedDoData.si.forEach((si) => {
                        siDropdown.append($("<option>", {value: si, text: si}));
                    });
                })
                .fail((error) => {
                    console.error("Error fetching JSON data:", error);
                });
        }
    }

    doDropdown.on("change", function (event) {
        const selectedValue = event.target.value;
        $("#address").val(selectedValue);
        updateSiDropdown(selectedValue);
    });

    siDropdown.on("change", function (event) {
        const selectedValue = event.target.value;
        $("#address").val(doDropdown.find(":selected").val() + " " + selectedValue);
    })

    populateDoDropdown();
});
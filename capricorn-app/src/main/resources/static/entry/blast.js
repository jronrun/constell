function blastReset(selector) {
    $(selector).blast(false);
}

function blast(target, selector, noneReset) {
    if (!noneReset) {
        blastReset(selector);
    }
    var blast = $(selector).blast({ search: target });
    blast.css({
        backgroundColor: "yellow",
        transition: "color 400ms"
    }); //.velocity({ backgroundColorAlpha: 1 }, { duration: 400 });
    return blast;
}
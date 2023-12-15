document.addEventListener('DOMContentLoaded', function () {
    const width = window.innerWidth;
    const height = window.innerHeight;

    // Erstellen des Stage-Objekts
    const stage = new Konva.Stage({
        container: 'container',  // ID des Div-Containers
        width: width,
        height: height,
    });

    // Erstellen eines Layers
    const layer = new Konva.Layer();
    stage.add(layer);

    let isPainting = false;
    let currentLine;
    let currentTool = 'pencil'; // Standardwerkzeug

    function drawLine(pos) {
        let strokeWidth = document.getElementById('brush-size').value;
        let color = currentTool === 'eraser' ? 'white' : document.getElementById('color-picker').value;

        currentLine = new Konva.Line({
            stroke: color,
            strokeWidth: strokeWidth,
            globalCompositeOperation: currentTool === 'eraser' ? 'destination-out' : 'source-over',
            points: [pos.x, pos.y],
            lineCap: 'round',
            lineJoin: 'round'
        });

        // Für den Pinsel weiche Kanten hinzufügen
        if (currentTool === 'brush') {
            currentLine.shadowColor(color);
            currentLine.shadowBlur(strokeWidth*2);
            currentLine.shadowOpacity(5);
            currentLine.opacity(0.7)
        }

        layer.add(currentLine);
    }


    stage.on('mousedown touchstart', function () {
        isPainting = true;
        const pos = stage.getPointerPosition();
        drawLine(pos);
    });

    stage.on('mousemove touchmove', function () {
        if (!isPainting) return;
        const pos = stage.getPointerPosition();
        const newPoints = currentLine.points().concat([pos.x, pos.y]);
        currentLine.points(newPoints);
        layer.batchDraw();
    });

    stage.on('mouseup touchend', function () {
        isPainting = false;
    });

    function setActiveTool(tool) {
        currentTool = tool;
        // Aktualisieren der Button-Stile
        document.querySelectorAll('.tool-button').forEach(function(button) {
            button.classList.remove('active');
        });
        document.getElementById('tool-' + tool).classList.add('active');
    }

    // Event-Listener für die Werkzeugauswahl
    document.getElementById('tool-pencil').addEventListener('click', function() {
        setActiveTool('pencil');
    });

    document.getElementById('tool-brush').addEventListener('click', function() {
        setActiveTool('brush');
    });

    document.getElementById('tool-eraser').addEventListener('click', function() {
        setActiveTool('eraser');
    });

    document.getElementById('clear').addEventListener('click', function() {
        layer.removeChildren();
        layer.draw();
    });

});

    document.addEventListener('keydown', onDocumentKeyDown, false);
    function onDocumentKeyDown(event) {
        var delta = 5 * Math.PI / 180;
        event = event || window.event;
        var keycode = event.keyCode;
        switch (keycode) {
            case 37: // left
                controls.rotate_left(-delta);
                break;
            case 39: // right
                controls.rotate_left(delta);
                break;
            case 38: // up
                controls.rotate_up(delta);
                break;
            case 40: // down
                controls.rotate_up(-delta);
                break;
        }
    }

    // added # Frank.L
    var rotateAngles = [];
    rotateAngles["right_sleeve2"] = Math.PI * 0.8;
    rotateAngles["right_wrist_girth"] = Math.PI * 0.5;
    rotateAngles["right_upper_arm_girth"] = Math.PI * 0.5;
    rotateAngles["nwaist_girth"] = -Math.PI;
    rotateAngles["hhip_girth"] = -Math.PI;
    rotateAngles["collar_girth"] = Math.PI * 0.5;
    rotateAngles['back_shoulder'] = -Math.PI;
    rotateAngles['left_shoulder'] = -Math.PI * 0.5;
	/*
    var ccircle = document.getElementById("viewReset");
    ccircle.addEventListener("click", function() {
        viewReset();
    }, false);
	*/
    function viewReset() {
        document.removeEventListener("mousedown", onDocumentAutoMouseDown, false);
        //soloPart("");
        cameraReset();
    }

    function cameraReset() {
        selectScene(Scene.Measure);
    }
    function togglePartView(_partname, _pt_name=null) {
        cameraReset();
        render();
        //enableClick = false;
        singleView(_partname);
    }
  
    function singleView(_partname) {
        switch (_partname) {
            case 'shoulder':
                controls.rotate_left(Math.PI);
                break;
            case 'legs':
                break;
            case 'side':
                controls.rotate_left(-Math.PI * 0.5);
                break;
        }
        controls.scope_update();
    }
   
    /*
    function shoulderLeftView() {
        controls.rotate_left(rotateAngles["left_shoulder"]);
        controls.rotate_up(Math.PI * 0.25);
        controls.pan_xy(0, 200);
        controls.dolly_out(0.5);
    }
    */
    function leftSleeveView() {
        controls.rotate_left(-rotateAngles["right_sleeve2"]);
        controls.rotate_up(-Math.PI * 0.15);
        controls.pan_xy(0, 150);
        controls.dolly_out(0.5);
    }
    function rightSleeveView() {
        controls.rotate_left(rotateAngles["right_sleeve2"]);
        controls.rotate_up(-Math.PI * 0.15);
        controls.pan_xy(0, 150);
        controls.dolly_out(0.5);
    }
    function bustView() {
        controls.pan_xy(0, 200);
        controls.dolly_out(0.5);
    }
    function collarView() {
        controls.rotate_left(rotateAngles["collar_girth"]);
        controls.rotate_up(Math.PI * 0.25);
        controls.pan_xy(0, 200);
        controls.dolly_out(0.5);
    }
    function nwaistView() {
        controls.rotate_left(rotateAngles["nwaist_girth"]);
        //controls.pan_xy(0, 100);
        controls.dolly_out(0.5);
    }
    function hhipView() {
        controls.rotate_left(rotateAngles['hhip_girth']);
        controls.dolly_out(0.5);
    }
    function thighView() {
        controls.pan_xy(50, -200);
        controls.dolly_out(0.5);
    }
    function wristView() {
        controls.rotate_left(rotateAngles["right_wrist_girth"]);
        controls.pan_xy(0, 100);
        controls.dolly_out(0.5);
    }
    function rightUpperarmView() {
        controls.rotate_left(rotateAngles["right_wrist_girth"]);
        controls.pan_xy(0, 100);
        controls.dolly_out(0.5);
    }
    function hhip2floorView(_alpha) {
        controls.rotate_left(_alpha);
        //controls.pan_xy(0, 100);
        //controls.dolly_out(0.5);
    }

    function leftUpperarmView() {
        controls.rotate_left(-rotateAngles['right_wrist_girth']);
        controls.pan_xy(0, 100);
        controls.dolly_out(0.5);
    }
    function backshoulderView() {
        controls.rotate_left(rotateAngles['back_shoulder']);
        controls.rotate_up(Math.PI * 0.35);
        controls.pan_xy(0, 200);
        controls.dolly_out(0.5);
    }
    function frontView(_y, _alpha) {
        controls.pan_xy(0, _y);
        controls.dolly_out(_alpha);
    }
    function backView(_y, _alpha) {
        controls.rotate_left(rotateAngles['back_shoulder']);
        controls.pan_xy(0, _y);
        controls.dolly_out(_alpha);
    }
    function uriseView() {
        controls.rotate_up(-Math.PI * 0.25);
        controls.dolly_out(0.5);
    }
    function toggleLeft(angle) {
        controls.rotate_left(angle);
    }
    function toggleUp(angle) {
        controls.rotate_up(angle);
    }
    // added # Frank.L

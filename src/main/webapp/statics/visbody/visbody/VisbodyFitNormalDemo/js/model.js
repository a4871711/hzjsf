var stats;
var controls;
var container;
var camera;
var light;
var scene;
var renderer;
var mainTexturePath = 'model/';

var registerObj;
var Scene = {Normal: "normal", HeatMap: "heatmap", Measure: "heatmap"};

//heat map
var textureMapSwitch = 0.0;
var textureMapMat;
var textureMapTex;
var metalness = 0.5;
var roughness = 0.9;

var TrunkLabel = {
    All: "all",
    Bust: "bust_girth",
    LeftUpperArm: "left_upper_arm_girth",
    RightUpperArm: "right_upper_arm_girth",
    Waist: "waist_girth",
    Hip: "hip_girth",
    LeftThigh: "left_thigh_girth",
    RightThigh: "right_thigh_girth",
    LeftCalf: "left_calf_girth",
    RightCalf: "right_calf_girth",
    None: "none"
};

var girth = {
    'left_upper_arm_girth':'左上臂围',
    'right_upper_arm_girth':'右上臂围',
    'bust_girth':'胸围',
    'waist_girth':'腰围',
    'hip_girth':'臀围',
    'right_thigh_girth':'右大腿围',
    'left_thigh_girth':'左大腿围',
    'right_calf_girth':'右小腿围',
    'left_calf_girth':'左小腿围',
};

var displayList = [
    "bust_girth", 
    "left_upper_arm_girth", 
    "right_upper_arm_girth", 
    "waist_girth", 
    "hip_girth",
    "left_thigh_girth", 
    "right_thigh_girth", 
    "left_calf_girth", 
    "right_calf_girth"
];

//gui
var gui;
var effectControl;

//Raycaster
var raycaster = new THREE.Raycaster();
var mouse = new THREE.Vector2();

//girth heatmap curve
var heatMapTexPath = "../model/texture/heatmap.jpg";

var rulerUVScale;
var AMOUNT_OF_POINTS;
var measureValues = [];
var points = [];
var rulerTexSelected;
var curves = [];
var touchCurves = [];
var neededFixCurves = [];
var curvesOffset = 1.0;
var yOffset = 900.0;
var keyCnt = 0;
const TOUCH_DELAY = 20;
var touchCnt = 0;
var touchStart = new THREE.Vector2();
var touchEnd = new THREE.Vector2();
var touchDelta = new THREE.Vector2();
var touchMoveCurveOrder = [];
const SAMPLE_INTERVAL_SCALE = 0.01;
var scale = 1.2;
var dist = 1000;

init();
control();
render();


function fitCameraToObject() {
    var bBox = new THREE.Box3().setFromObject(registerObj);
    var height = bBox.getSize().y;
    yOffset = height * 0.5;
    dist = height / (2 * Math.tan(camera.fov * Math.PI / 360));
}

//To Front-end: choose a scene to show
function selectScene(selected) {
    switch (selected) {
        case Scene.Normal:
            resetControl(true, false, true);
            removeTouchEvent(true);
            removeCurveFromScene(true);
            isHeatMapSwitchOn(false);
            break;
        case Scene.HeatMap:
            resetControl(true, false, true);
            removeTouchEvent(true);
            removeCurveFromScene(true);
            isHeatMapSwitchOn(true);
            break;
        case Scene.Measure:
            resetControl(true, false, true);
            removeTouchEvent(false);
            removeCurveFromScene(false);
            selectCurve(0, touchMoveCurveOrder[0]);
            isHeatMapSwitchOn(false);
            break;
        default:
            console.error("SelectScene error. Invalid scene: " + selected);
            break;
    }
}

//To Front-end: update heatmap texture
function updateTextureMapTexture(path){
    textureMapTex.dispose();
    textureMapTex = new THREE.TextureLoader().load(path);
    updateMaterial();
}

//To Front-end: return obj loading process
function onLoadOBJProgress(xhr) {
    if (xhr.lengthComputable) {
        var percentComplete = xhr.loaded / xhr.total * 100;
        console.log(Math.round(percentComplete, 2) + '% OBJ Downloaded');
        // $('#pro').html(Math.round(percentComplete, 2));
    }
}

//TO Front-end: handle obj loading error
function onLoadOBJError(xhr) {
}


function initCamera() {
    var camera = new THREE.PerspectiveCamera(45, container.offsetWidth / container.offsetHeight, 50, 10000);
    camera.position.set(0, 1300, 2200);
    return camera;
}



function initLightMain() {
    var light = new THREE.DirectionalLight(0xD5D5D5, 0.7);
    light.position.set(0.0, 3000.0, 3000.0);
    light.target.position.set(0.0, 0.0, 0.0);
    return light;
}

function initLight0() {
    var light = new THREE.DirectionalLight(0xD5D5D5, 0.5);
    light.position.set(-2000.0, 1000.0, -1000.0);
    light.target.position.set(1000.0, 200.0, 0.0);
    return light;
}

function initLight1() {
    var light = new THREE.DirectionalLight(0xD5D5D5, 0.5);
    light.position.set(2000.0, 0.0, -1000.0);
    light.target.position.set(0.0, 0.0, 0.0);
    return light;
}

function initLight2() {
    var light = new THREE.DirectionalLight(0xD5D5D5, 0.2);
    light.position.set(0.0, 500.0, 1000.0);
    light.target.position.set(0.0, 1000.0, 0.0);
    return light;
}


function init() {
    container = document.getElementById('model');
    renderer = new THREE.WebGLRenderer({alpha: true, antialias: true});
    renderer.setPixelRatio(window.devicePixelRatio);
    renderer.setSize(container.offsetWidth, container.offsetHeight);
    container.appendChild(renderer.domElement);
    scene = new THREE.Scene();
    
    camera = initCamera();
    scene.add(camera);
    light = initLightMain();
    //camera.add(light.target);
    camera.add(light);

    light0 = initLight0();
    camera.add(light0.target);
    camera.add(light0);

    light1 = initLight1();
    camera.add(light1.target);
    camera.add(light1);

    light2 = initLight2();
    camera.add(light2.target);
    camera.add(light2);

    window.addEventListener('resize', onWindowResize, false);

    initTouchMoveCurveOrder();
}

//connect point to line, get point world position from json
function initMeasureCurveFromJSON(jsonPath) {
    clearCurve();
    $.getJSON(jsonPath, function (data) {
        $.each(data, function (key, val) {
            if (val.value && displayList.indexOf(key) > -1) {
                measureValues[key] = val.value;

                if (val.pointList != null) {
                    var points = [];
                    points.name = key;
                    $.each(val.pointList, function (k, v) {
                        var p = new THREE.Vector3(v[0], v[1], v[2]);
                        points.push(p);
                        //createSphere(p, 0.5);
                    });
                    //create a loop
                    if(true){
                        AMOUNT_OF_POINTS = points.length;
                        var endp = new THREE.Vector3(points[0].x*0.8, points[0].y*0.8, points[0].z*0.8);
                        endp.x += points[1].x*0.2;
                        endp.y += points[1].y*0.2;
                        endp.z += points[1].z*0.2;
                        points.push(endp);
                        drawCurve(initPointsByPosition(points, curvesOffset, true));
                    } else {
                        // console.error("InitMeasureCurveFromJSON Error. " + key + " Invalid Points. length:" + points.length + "  Do Not Draw Curve.");
                    }

                } else {
                    // console.log("InitMeasureCurveFromJSON Error."+ key +  "PointList is null. Do Not Draw Curve.");
                }
            }else{
                // console.error("InitMeasureCurveFromJSON Error." + key + " Value is null.Do Not Draw Curve.");
            }

        });
        //some exceptions occurs when the plane of points is not parallel with the xz_plane
        for (var k in neededFixCurves) {
            rotateBack(k, neededFixCurves[k]);
        }
        console.log('Draw Over');

        drawOver();
    });
}

function selectCurve(delta, curveName) {
    var keys = Object.keys(curves);
    if (keys.length > 0) {
        for (var key in curves) {
            curves[key].material.uniforms.uRulerControl.value = new THREE.Vector3(0.2, 0.2, 0.2);
            curves[key].material.uniforms.uVertexOffset.value = 0.0;
        }

        var name = curveName;
        if (!name) {
            delta > 0 ? keyCnt++ : keyCnt--;
            if (keyCnt > keys.length - 1) keyCnt = keys.length - 1;
            if (keyCnt < 0) keyCnt = 0;
            name = touchMoveCurveOrder[keyCnt];
        }

        if (curves[name]) {
            curves[name].material.uniforms.uRulerControl.value = new THREE.Vector3(1.0, 1.0, 1.0);
            curves[name].material.uniforms.uVertexOffset.value = 1.0;
            // console.log("Select Curve. " + name + " value: " + measureValues[name]);
            // selectCurveValue(measureValues[name]);
            $.each(girth, function (key, val) {
                if(key == name){
                    var value =  measureValues[name] * 0.1;
                    selectCurveValue(val,value.toFixed(1),name);
                }
            });
        } else {
            console.log("Select Curve Error. Curve: " + name + " Mesh Invalid.")
        }
    }
}


//init material & texture & uniforms
function initMaterial(textureMapTexPath) {
    var vertShader = document.getElementById('vs').textContent;
    var fragShader = document.getElementById('fs').textContent;

    textureMapTex = new THREE.TextureLoader().load(textureMapTexPath);

    var uniforms = {
        uFullTextureMapTex:{type:'t', value:null},
        uTextureMapSwitch: {type: 'f', value: textureMapSwitch},
        emissive:{type:"c", value:new THREE.Color(0x111111)},
        metalness:{type:"f", value:metalness},
        roughness:{type:"f", value:roughness},
    };

    textureMapMat = new THREE.ShaderMaterial({
        uniforms: THREE.UniformsUtils.merge(
                [THREE.ShaderLib['physical'].uniforms, uniforms]),
        vertexShader: "#define USE_TEXTUREMAP\n" + vertShader,
        fragmentShader: "#define USE_TEXTUREMAP\n" + fragShader,
        wireframe: false,
        lights: true,
        transparent: false,
    });

    textureMapMat.uniforms.uFullTextureMapTex.value = textureMapTex;
    return textureMapMat;
}

function isHeatMapSwitchOn(isOn) {
    textureMapSwitch = isOn ? 1.0 : 0.0;
    if(isOn){
        metalness = 0.0;
        roughness = 1.0;
    }else{
        metalness = 0.5;
        roughness = 0.7;
    }
    updateMaterial();
}


//update heatmap glsl uniforms
function updateMaterial() {
    if (textureMapMat == null) {
        console.error("UpdateMaterial Error. Return.");
        return;
    }

    var uniforms = textureMapMat.uniforms;
    if(uniforms.uFullTextureMapTex != null) uniforms.uFullTextureMapTex.value = textureMapTex;
    if(uniforms.uTextureMapSwitch != null) uniforms.uTextureMapSwitch.value = textureMapSwitch;
    if(uniforms.metalness != null) uniforms.metalness.value = metalness;
    if(uniforms.roughness != null) uniforms.roughness.value = roughness;

}

//handle mousedown event
function onDocumentMouseDown(event) {
    if (event.changedTouches) {
        x = event.changedTouches[0].pageX;
        y = event.changedTouches[0].pageY;
    } else {
        x = event.clientX;
        y = event.clientY;
    }

    y = y - $('canvas').offset().top;
    x = x - $('canvas').offset().left;

    mouse.x = ( x / container.offsetWidth ) * 2 - 1;
    mouse.y = -( y / container.offsetHeight ) * 2 + 1;
    checkIntersection();
}


//load obj from a specific path
function loadOBJ(filePath, textureMapTexPath, scale, pos, rot) { 
    var mat = initMaterial(textureMapTexPath);

    var s = arguments[2] ? arguments[2] : 1.0;
    var p = arguments[3] ? arguments[3] : new THREE.Vector3(0, 0, 0);
    var r = arguments[4] ? arguments[4] : new THREE.Vector3(0, 0, 0);
    var onLoad = function (object) {
        object.traverse(function (child) {
            if (child instanceof THREE.Mesh) {
                var geometry = new THREE.Geometry().fromBufferGeometry( child.geometry );
                geometry.computeFaceNormals();
                geometry.mergeVertices();
                geometry.computeVertexNormals();
                child.geometry = new THREE.BufferGeometry().fromGeometry( geometry );

                child.material = mat;
                child.material.shading = THREE.SmoothShading;

            }
            object.scale.set(s, s, s);
            object.rotation.set(r.x * Math.PI / 180.0, r.y * Math.PI / 180.0, r.z * Math.PI / 180.0);
            object.position.set(p.x, p.y, p.z);
            registerObj = object;
            scene.add(object);
        });
        initScene();
        loadOBJOver();
    };

    var loader = new THREE.OBJLoader();
    loader.load(filePath, onLoad, onLoadOBJProgress, onLoadOBJError);
}

function loadOBJOver(){
    console.log("加载完成");
}
function initScene() {
    fitCameraToObject();
    selectScene(Scene.Normal);
}

//raycaster detected
function checkIntersection() {
    raycaster.setFromCamera(mouse, camera);
    var touchs = [];
    for (var key in touchCurves) {
        touchs.push(touchCurves[key]);
    }

    var intersects = raycaster.intersectObjects(touchs, true);
    if (intersects.length > 0) {
        selectCurve(0, intersects[0].object.name);
    }
}


function calculateCenterPos(points) {
    var centerPos = new THREE.Vector3(0, 0, 0);
    for (var i = 0; i < points.length; i++) {
        centerPos.add(points[i]);
    }
    centerPos.divideScalar(points.length);
    return centerPos;
}


//calculate the normal of points_plane
function calculateUPVector(points, center) {
    if (points.length < 2) {
        console.error("CalculateUPVector Error. Points Length < 2 . Return.");
        return;
    }
    var vec1 = points[0].clone().sub(center).normalize();
    var vec2 = points[1].clone().sub(center).normalize();
    // return vec1.clone().cross(vec2).normalize();
    return vec2.clone().cross(vec1).normalize();
}

//calculate points position
function initPointsByPosition(points, pointOffsetScale, needFix) {
    var centerPos = calculateCenterPos(points);

    var isNeedFix = arguments[2] ? arguments[2] : false;
    var upVector;
    if (isNeedFix) {
        upVector = calculateUPVector(points, centerPos);
        neededFixCurves[points.name] = upVector;
    }
    //createCube(centerPos, 1.0);

    for (var i = 0; i < points.length; i++) {
        var dir = points[i].clone().sub(centerPos).normalize();
        //points[i].add(dir.multiplyScalar(Math.max(1.0, pointOffsetScale)));
        points[i].add(dir.multiplyScalar(pointOffsetScale));
        if (isNeedFix) rotateTo(points[i], upVector);
    }
    return points;
}

//init uniforms for girth curve material
function initMeasureMaterial() {
    var vertShader = document.getElementById('vs').textContent;
    var fragShader = document.getElementById('fs').textContent;

    //if(rulerTexSelected == null) {
    //    rulerTexSelected = new THREE.TextureLoader().load(mainTexturePath + 'rulerd.jpg');
    //    rulerTexSelected.wrapS = rulerTexSelected.wrapT = THREE.RepeatWrapping;
    //    rulerTexSelected.magFilter = THREE.LinearFilter;
    //}

    var uniforms = {
        emissive: {type: "c", value: new THREE.Color(0xEEEEEE)},
        uRulerTexSelected: {type: "t", value: null},
        uRulerControl: {type: "v3", value: new THREE.Vector3(0.0, 0.0, 0.0)},
        uVertexOffset: {type: 'f', value: 0.0},
        uRulerUVScale: {type: "f", value: 1.0}
    };

    var mat = new THREE.ShaderMaterial({
        uniforms: THREE.UniformsUtils.merge(
                [//THREE.UniformsLib['lights'],
                    THREE.ShaderLib['physical'].uniforms, uniforms]),
        vertexShader: "#define USE_MEASURE\n" + vertShader,
        fragmentShader: "#define USE_MEASURE\n" + fragShader,
        polygonOffset: true,
        polygonOffsetFactor: -8,
        polygonOffsetUnits: 0,
        wireframe: false,
        lights: true,
        transparent: true
    });
    mat.uniforms.uRulerTexSelected.value = rulerTexSelected;
    return mat;
}


//rotate slanted points  to parallel the xz_plane
function rotateTo(pos, upVector) {
    var quaternion = new THREE.Quaternion();
    quaternion.setFromUnitVectors(upVector, new THREE.Vector3(0.0, 1.0, 0.0));
    pos.applyQuaternion(quaternion);
}

//rotate back
function rotateBack(label, upVector) {
    if (!curves[label]) {
        console.error("Fix curve mesh error. The curve is invalid.Return.");
        return;
    }

    var quaternion = new THREE.Quaternion();
    quaternion.setFromUnitVectors(new THREE.Vector3(0.0, 1.0, 0.0), upVector);
    curves[label].setRotationFromQuaternion(quaternion);

    if (!touchCurves[label]) {
        console.error("Fix curve mesh error. The TouchCurve is invalid.Return.");
        return;
    }
    touchCurves[label].setRotationFromQuaternion(quaternion);

}


function clearCurve() {
    if (curves) {
        for(var k in curves){
            scene.remove(curves[k]);
            delete curves[k];
        }
    }

    if(touchCurves){
        for(var k in touchCurves){
            scene.remove(touchCurves[k]);
            delete touchCurves[k];
        }
    }

    if(neededFixCurves){
        for(var k in neededFixCurves){
            delete neededFixCurves[k];
        }
    }
}



//draw curve mesh with TubeBufferGeometry
function drawCurve(points) {
    var curve = new THREE.CatmullRomCurve3(points);
    curve.name = points.name;
    var tube = new THREE.TubeBufferGeometry(curve, 64, 10, 6, false);

    vertexOffset = 0.0;
    var mat = initMeasureMaterial();
    mat.uniforms.uRulerUVScale.value = measureValues[points.name] * SAMPLE_INTERVAL_SCALE;
    var tubeMesh = new THREE.Mesh(tube, mat);
    tubeMesh.material.polygonOffset = true;
    tubeMesh.material.depthTest = true;
    tubeMesh.material.polygonOffsetFactor = 1;
    tubeMesh.material.polygonOffsetUnits = 4.0;
    tubeMesh.name = points.name;
    curves[points.name] = tubeMesh;
    drawTouchCurve(curve);
}


//set the order of showing curver when touch move
function initTouchMoveCurveOrder() {
    touchMoveCurveOrder[0] = TrunkLabel.Bust;
    touchMoveCurveOrder[1] = TrunkLabel.LeftUpperArm;
    touchMoveCurveOrder[2] = TrunkLabel.RightUpperArm;
    touchMoveCurveOrder[3] = TrunkLabel.Waist;
    touchMoveCurveOrder[4] = TrunkLabel.Hip;
    touchMoveCurveOrder[5] = TrunkLabel.LeftThigh;
    touchMoveCurveOrder[6] = TrunkLabel.RightThigh;
    touchMoveCurveOrder[7] = TrunkLabel.LeftCalf;
    touchMoveCurveOrder[8] = TrunkLabel.RightCalf;
}


//curve for responding touch event
function drawTouchCurve(curve) {
    var touchTube = new THREE.TubeBufferGeometry(curve, AMOUNT_OF_POINTS, 70, 2, true);
    var touchMat = new THREE.MeshBasicMaterial({visible: false});
    var touchTubeMesh = new THREE.Mesh(touchTube, touchMat);
    touchTubeMesh.name = curve.name;
    touchCurves[curve.name] = touchTubeMesh;
}


function removeCurveFromScene(isReomove) {
    if (curves) {
        for (var k in curves) {
            if (isReomove) {
                scene.remove(curves[k]);
            } else {
                scene.add(curves[k]);
            }
        }
    }

    if (touchCurves) {
        for (var k in touchCurves) {
            if (isReomove) {
                scene.remove(touchCurves[k]);
            } else {
                scene.add(touchCurves[k]);
            }
        }
    }
}


function createSphere(p, s) {
    var sphere = new THREE.Mesh(new THREE.SphereGeometry(10 * s, 32, 32), new THREE.MeshBasicMaterial({color: 0xff0000}));
    sphere.position.set(p.x, p.y, p.z);
    scene.add(sphere);
    return sphere;
}

function render() {
    id=requestAnimationFrame(render);
    renderer.render(scene, camera);
}


function onMouseDown(event) {
    touchStart.set(event.clientX, event.clientY);
}

function onTouchStart(event) {
    touchStart.set(event.touches[0].pageX, event.touches[0].pageY);
}

//touch move to select the curve[mobile]
function onTouchMove(event) {
    touchEnd.set(event.touches[0].pageX, event.touches[0].pageY);
    touchDelta.subVectors(touchEnd, touchStart);

    touchStart.copy(touchEnd);
    touchCnt++;
    if (touchCnt > TOUCH_DELAY) {
        touchCnt = 0;
        selectCurve(touchDelta.y);
    }
}

//remove the touch event at specific scene
function removeTouchEvent(isRemove) {
    if (isRemove) {
        document.removeEventListener("mousedown", onMouseDown, false);
        document.removeEventListener("mousedown", onDocumentMouseDown, false);
        document.removeEventListener("touchstart", onTouchStart, false);
        document.removeEventListener("touchstart", onDocumentMouseDown, false);
        document.removeEventListener("touchmove", onTouchMove, false);
    } else {
        document.addEventListener("mousedown", onMouseDown, false);
        document.addEventListener("mousedown", onDocumentMouseDown, false);
        document.addEventListener("touchstart", onTouchStart, false);
        document.addEventListener("touchstart", onDocumentMouseDown, false);
        document.addEventListener("touchmove", onTouchMove, false);
    }
}

//init orbitcontrol
function control() {
    controls = new THREE.OrbitControls(camera, renderer.domElement);
    controls.target = new THREE.Vector3(0.0, yOffset, 0.0);
    controls.enableRotate = true;
    controls.enableZoom = false;
    controls.enablePan = false;
    controls.update();
}


//reset orbitcontrol
function resetControl(enableRotate, enableZoom, enablePan) {
    if (controls) {
        controls.reset();
        //camera.position.set(camera.position.x, camera.position.y, dist * scale);
        camera.position.set(camera.position.x, yOffset, dist * scale);
        controls.target = new THREE.Vector3(0.0, yOffset, 0.0);
        controls.enableRotate = arguments[0] ? arguments[0] : false;
        controls.enableZoom = arguments[1] ? arguments[1] : false;
        controls.enablePan = arguments[2] ? arguments[2] : false;
        controls.update();
    }
}


function onWindowResize() {
    SCREEN_WIDTH = container.offsetWidth;
    SCREEN_HEIGHT = container.offsetHeight;
    renderer.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
    camera.aspect = SCREEN_WIDTH / SCREEN_HEIGHT;
    camera.updateProjectionMatrix();
}
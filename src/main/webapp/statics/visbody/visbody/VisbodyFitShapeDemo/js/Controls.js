var controls;
var container;
var camera;
var scene;
var renderer;
var mainTexturePath = 'model/texture/';
var registerObj;
var Scene = {Normal: "normal",  Measure: "measure"};
var mouse = new THREE.Vector2();
//var filePath = "model/3dkeypoints.txt";
//var modelPath = "model/front.obj";
var yOffset;
var scale = 1.1;
var dist = 1000;
var texture;
var bgTex;

var points = [];
var mesh;

init();
control();

//scene.background = bgTex;
render();

function destroy() {
    if (texture === null || texture === undefined) {}
    else {
        texture.dispose();
    }
    while (scene.children.length > 0) {
        _geom = scene.children[0].geometry;
        if (_geom === null || _geom === undefined) {}
        else {
            _geom.dispose();
        }
        _mat = scene.children[0].material;
        if (_mat === null || _mat === undefined) {}
        else {
            _mat.dispose();
        }
        scene.remove(scene.children[0]);
    }
}

function fitCameraToObject() {
    var bBox = new THREE.Box3().setFromObject(registerObj);
    var height = bBox.getSize().y;
    yOffset = height * 0.5;
    dist = height / (2 * Math.tan(camera.fov * Math.PI / 360));
}

function initScene() {
    fitCameraToObject();
    selectScene(Scene.Measure);
}
function swapArray(verts) {
    var n = verts.length;
    for (var i = 0; i <= n / 2; i++) {
        var tmp = verts[i];
        verts[i] = verts[n-1-i];
        verts[n-1-i] = tmp;
    }
}
function removeAll() {
    scene.remove(scene.getObjectByName('shoulder'));
    scene.remove(scene.getObjectByName('right_leg'));
    scene.remove(scene.getObjectByName('left_leg'));
    scene.remove(scene.getObjectByName('legs_points'));
    scene.remove(scene.getObjectByName('shoulder_points'));
    scene.remove(scene.getObjectByName('side_shoulder_points'));
    scene.remove(scene.getObjectByName('side_shoulder_up'));
    scene.remove(scene.getObjectByName('side_shoulder_down'));
}
function unvisibleAll() {
    ['shoulder', 'right_leg', 'left_leg', 'legs_points', 'shoulder_points', 'side_shoulder_points', 'side_shoulder_up', 'side_shoulder_down'].forEach(function(_val) {
        _obj = scene.getObjectByName(_val);
        if (_obj === null || _obj === undefined) {
            //
        }
        else {
            _obj.visible = false;
        }
    });
}
function drawRightLeg() {
    var _pts = [];
    for (var _i = 8*3; _i < 24+9; _i++) { // 8, 9, 10
        _pts.push(points[_i]);
        console.log(points[_i]);
    }
    _pts.name = 'right_leg';
    drawPointsLine(_pts);
}
function drawLeftLeg() {
    var _pts_left = [];
    for (_i = 11*3; _i < 33+9; _i++) { // 11 12 13
        _pts_left.push(points[_i]);
    }
    _pts_left.name = 'left_leg';

    drawPointsLine(_pts_left);
}
function drawLegPoints() {
    var pts = [points[24], points[25], points[26],
        points[30], points[31], points[32],
        points[33], points[34], points[35],
        points[39], points[40], points[41]];
    createPoints(pts, 'legs_points');
}
function drawBack() {
    _pts = [];
    _pts.push(points[6], points[7], points[8]);   // point 2.
    _pts.push(points[15], points[16], points[17]); // point 5.
    _pts.name = 'shoulder';
    drawPointsLine(_pts);
}
function drawBackPoints() {
    _pts = [];
    _pts.push(points[6], points[7], points[8]);   // point 2.
    _pts.push(points[15], points[16], points[17]); // point 5.
    createPoints(_pts, 'shoulder_points');
}
function drawSideLeft() {
    _pts = [points[3], points[4], points[5], // 1
            points[33], points[34], points[35], // 11
            points[39], points[40], points[41], // 13
            points[17*3], points[52], points[53]]; // 17
    createPoints(_pts, 'side_shoulder_points');
}
function drawSideUp() {
    _pts = [];
    _pts.push(points[3], points[4], points[5], // 1
        points[33], points[34], points[35]);
    _pts.name = 'side_shoulder_up';
    drawPointsLine(_pts);
}
function drawSideDown() {
    _pts = [];
    _pts.push(points[33], points[34], points[35], // 11
        points[39], points[40], points[41]);
    _pts.name = 'side_shoulder_down';
    drawPointsLine(_pts);
}
function showBack() {
    unvisibleAll();
    ['shoulder', 'shoulder_points'].forEach(function(_name) {
        _obj = scene.getObjectByName(_name);
        if (_obj === null || _obj === undefined) {
            if (_name === 'shoulder') {
                drawBack();
            } else {
                drawBackPoints();
            }
        } else {
            _obj.visible = true;
        }
    });
}
function showFront() {
	unvisibleAll();
	['right_leg', 'left_leg', 'legs_points'].forEach(function(_name) {
        _obj = scene.getObjectByName(_name);
        if (_obj === null || _obj === undefined) {
            switch (_name) {
                case 'right_leg':
                    drawRightLeg();
                    break;
                case 'left_leg':
                    drawLeftLeg();
                    break;
                case 'legs_points':
                    drawLegPoints();
                    break;
            }
        }
        else {
            _obj.visible = true;
        }
    });
}

function showSide(_isRight=false) {
	unvisibleAll();
    ['side_shoulder_points', 'side_shoulder_up', 'side_shoulder_down'].forEach(function(_name) {
        _obj = scene.getObjectByName(_name);
        if (_obj === null || _obj === undefined) {
            switch (_name) {
                case 'side_shoulder_points':
                    drawSideLeft();
                    break;
                case 'side_shoulder_up':
                    drawSideUp();
                    break;
                case 'side_shoulder_down':
                    drawSideDown();
                    break;
            }
        } else {
	       _obj.visible = true;
        }
    });
}

function keyDown(event) {
    switch (event.keyCode) {
        case 81: //q
            showFront();
            break;
        case 80: // p
            showBack();
            break;
        case 79: // o
            showSide();
			break;
        case 65://a
            var strMime = "image/png";
            var imgData = renderer.domElement.toDataURL(strMime, 1.0);
            var strDownloadMime = "image/octet-stream";
            saveFile(imgData.replace(strMime, strDownloadMime), "abc.png");
            break;
        case 77: //m
            removeAll();
            togglePartView('shoulder');
			break;
        case 78: // n
            removeAll();
            togglePartView('side');
			break;
    }
}
//To Front-end: choose a scene to show
function selectScene(selected) {
    switch (selected) {
        case Scene.Measure:
            resetControl(true,false,true);
            break;
        default:
            console.error("SelectScene error. Invalid scene: " + selected);
            break;
    }
}

//To Front-end: return obj loading process
function onLoadOBJProgress(xhr) {
    if (xhr.lengthComputable) {
        var percentComplete = xhr.loaded / xhr.total * 100;
        console.log(Math.round(percentComplete, 2) + '% OBJ Downloaded');
    }
}

//TO Front-end: handle obj loading error
function onLoadOBJError(xhr) {
   console.log("ERROR! onLoadOBJ()");
}

function initCamera() {
    var camera = new THREE.PerspectiveCamera(45, container.offsetWidth / container.offsetHeight, 50, 10000);
    camera.position.set(0, 1300, 2200);

    return camera;
}

function init() {
    container = document.getElementById('model');

    renderer = new THREE.WebGLRenderer({alpha: true, antialias: true, preserveDrawingBuffer: true});
    //renderer = new THREE.WebGLRenderer({antialias: true, alpha: true});
    //renderer.sortObjects = false;
    renderer.setPixelRatio(window.devicePixelRatio * 2);
    renderer.setSize(container.offsetWidth, container.offsetHeight);
    //renderer.sortObjects = false;
    container.appendChild(renderer.domElement);

    scene = new THREE.Scene();
    camera = initCamera();
    scene.add(camera);
   
    var loader = new THREE.TextureLoader();
    texture = loader.load('model/texture/disc.png');
    //bgTex = loader.load("model/texture/background.png");

    window.addEventListener('resize', onWindowResize, false);
    document.addEventListener('keydown', keyDown, false);
    // initFile();
}


function initFile() {
    var reader = new THREE.FileLoader();
    reader.load(filePath, 
        function(data) { // function when resource is loaded.
        //console.log(data);
        var lines = data.split('\n');
        for (var line = 0; line < lines.length; line++) {
            var xyz = lines[line].split(/[\s*\t]/);
          
            xyz.forEach(function(val) {
                if (val) {
                    points.push(parseFloat(val));
                }
            });
        }
        showFront();
        //drawPoints();
    }, 
    function (xhr) { // function called when download progresses.
        //console.log(xhr.loaded, xhr.total, (xhr.loaded / xhr.total * 100) + '% loaded');
    },
    function (xhr) { // function called when download errors;
        console.error('An error happened!');
    }
    );
}

function drawPoints() {
    //console.log(points);
    var positions = new Float32Array(points.length);
    var i = 0;
    points.forEach(function(val) {
        positions[i] = points[i];
        i++;
    });
    var geometry = new THREE.BufferGeometry();
    geometry.addAttribute('position', new THREE.BufferAttribute(positions, 10));
    var material = new THREE.PointsMaterial( {color: 0x00FF00, size: 25} );
    var particles = new THREE.Points(geometry, material);
    material.dispose();
    material = null;
    geometry.dispose();
    geometry = null;

    /*
    var geometry = new THREE.SphereBufferGeometry(5, 32, 32);
    var material = new THREE.MeshBasicMaterial( {color: 0x008888 });
    var particles = new THREE.Mesh(geometry, material);
    particles.position.set(positions[0], positions[1], positions[2]);
    */
    scene.add(particles);
}

saveFile = function (strData, filename) {
 	var link = document.createElement('a');
 	if (typeof link.download === 'string' )
 	{
 		document.body.appendChild(link);
 		link.download = filename;
 		link.href = strData;
 		link.click();
 		document.body.removeChild(link);
 	}
 	else {
 		location.replace(uri);
 	}
};

//load obj from a specific path
function loadOBJ(filePath, scale, pos, rot) {
    var s = arguments[2] ? arguments[2] : 1.0;
    var p = arguments[3] ? arguments[3] : new THREE.Vector3(0, 0, 0);
    var r = arguments[4] ? arguments[4] : new THREE.Vector3(0, 0, 0);
    var onLoad = function (object) {
        object.traverse(function (child) {
            if (child instanceof THREE.Mesh) {

                var geometry = new THREE.Geometry().fromBufferGeometry( child.geometry );
                //geometry.computeFaceNormals();
                geometry.mergeVertices();
                //geometry.computeFaceNormals();
                geometry.computeVertexNormals();
                child.geometry = new THREE.BufferGeometry().fromGeometry( geometry );
                geometry.dispose();
                geometry = null;
                var path = "model/texture/white/";
                var format = '.jpg';
                var urls = [
                    path + 'posx' + format, path + 'negx' + format,
                    path + 'posy' + format, path + 'negy' + format,
                    path + 'posz' + format, path + 'negz' + format
                ];
                var textureCube = new THREE.CubeTextureLoader().load( urls );
                //textureCube.mapping = THREE.CubeRefractionMapping;
                //textureCube.format = THREE.RGBFormat;
             
                var shader = THREE.FresnelShader;
                var uniforms = THREE.UniformsUtils.clone(shader.uniforms);
                uniforms[ "tCube" ].value = textureCube;
                //uniforms["mRefractionRatio"].value = 0.48;
                var material = new THREE.ShaderMaterial( {
                    uniforms: uniforms,
                    vertexShader: shader.vertexShader,
                    fragmentShader: shader.fragmentShader,
                    transparent: true,
                    //opacity: 0.01,
                    depthTest: false,
                    depthWrite: false
                });

                mesh = new THREE.Mesh(child.geometry, material);
                material.dispose();
                material = null;
            }
            registerObj = object;

            if (mesh) {
                scene.add(mesh);
                //scene.add(object);
            }
        });
        initScene();
        initFile();
    };

    var loader = new THREE.OBJLoader();
    loader.load(filePath, onLoad, onLoadOBJProgress, onLoadOBJError);
}

function drawPointsLine(points) {
    if (points) {
        var dir, curve;
        var offset = 9;
        if (points.length === 9) {
            dir = new THREE.Vector3(points[6], points[7], points[8]).sub(new THREE.Vector3(points[0], points[1], points[2])).normalize();
            dir.multiplyScalar(offset);
            curve = new THREE.LineCurve3(new THREE.Vector3(points[0], points[1], points[2]).add(dir),
                                            new THREE.Vector3(points[6], points[7], points[8]).sub(dir));
        } else if (points.length === 6) {
            dir = new THREE.Vector3(points[3], points[4], points[5]).sub(new THREE.Vector3(points[0], points[1], points[2])).normalize();
            dir.multiplyScalar(offset);
            curve = new THREE.LineCurve3(new THREE.Vector3(points[0], points[1], points[2]).add(dir),
                                            new THREE.Vector3(points[3], points[4], points[5]).sub(dir));
        }
        curve.name = points.name;
        //var geometry = new THREE.TubeBufferGeometry(curve, 32, 2, 6, false);
        var geometry = new THREE.TubeBufferGeometry(curve, 32, 5, 6, false);
                                                 //(path, tubularSegments, radius, radiusSegments, closed
        var material = new THREE.MeshBasicMaterial( {color: 0x313a31} );

        var tubemesh = new THREE.Mesh(geometry, material);
        material.dispose();
        material = null;
        geometry.dispose();
        geometry = null;
        tubemesh.name = points.name;
        tubemesh.renderOrder = 0;
        scene.add(tubemesh);
    }
}

function createSphere(p, s) {
    var _geom = new THREE.SphereGeometry(10 * s, 32, 32);
    var _mat = new THREE.MeshBasicMaterial({color: 0x0000FF});
    var sphere = new THREE.Mesh(_geom, _mat);
    _mat.dispose();
    _mat = null;
    _geom.dispose();
    _geom = null;
    sphere.position.set(p.x, p.y, p.z);
    scene.add(sphere);
    return sphere;
}
function createPoints(pts, name) {
    var positions = new Float32Array(pts.length);
    var i = 0;
    pts.forEach(function(val) {
        positions[i] = val;
        i++;
    });

    var geometry = new THREE.BufferGeometry();
    geometry.addAttribute('position', new THREE.BufferAttribute(positions, 3));
    //geometry.setDrawRange(0, pts.length / 3);

    var pointsMaterial = new THREE.PointsMaterial( {color: 0x0000ff, map: texture, size: 85, alphaTest: 0.5} );
    var points = new THREE.Points(geometry, pointsMaterial);
    pointsMaterial.dispose();
    pointsMaterial = null;
    geometry.dispose();
    geometry = null;
    //geometry.renderOrder = 0;
    points.renderOrder = 0;
    points.name = name;
    scene.add(points);
}

function render() {
    id=requestAnimationFrame(render);
    renderer.render(scene, camera);
}

function onMouseDown(event) {
    touchStart.set(event.clientX, event.clientY);
    touchStart.set(event.clientX, event.clientY);
}

//init orbitcontrol
function control() {
    controls = new THREE.OrbitControls(camera, renderer.domElement);
    controls.target = new THREE.Vector3(0.0, yOffset, 0.0);
    controls.enableRotate = true;
    controls.enableZoom = true;
    controls.enablePan = false;
    controls.minDistance = 1000;
    controls.maxDistance = 5000;
    controls.maxPolarAngle = Math.PI / 2;
    controls.update();
}

//reset orbitcontrol
function resetControl(enableRotate, enableZoom, enablePan) {
    if (controls) {
        controls.reset();
        camera.position.set(camera.position.x, yOffset, dist * scale);

        controls.target = new THREE.Vector3(0.0, yOffset, 0.0);
        controls.enableRotate = arguments[0] ? arguments[0] : false;
        controls.enableZoom = arguments[1] ? arguments[1] : false;
        controls.enablePan = arguments[2] ? arguments[2] : false;
        controls.maxPolarAngle = controls.minPolarAngle = Math.PI / 2;
        //controls.maxAzimuthAngle = Math.PI;
        //controls.minAzimuthAngle = -Math.PI;
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

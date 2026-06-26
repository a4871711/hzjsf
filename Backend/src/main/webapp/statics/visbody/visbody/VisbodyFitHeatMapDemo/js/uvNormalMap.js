function updateUv(uv, uvIndex) {
    // update uv of BufferGeometry of registerObj.
    var _geom = registerObj.children[0].geometry;
    if (_geom === null || _geom === undefined) {
        console.log('error!');
        return;
    }

    var geometry = new THREE.Geometry().fromBufferGeometry(_geom);
    geometry.computeFaceNormals();
    geometry.mergeVertices();
    geometry.computeVertexNormals();

    var _faces = geometry.faces;
    var _verts = geometry.vertices;
    var _n = _faces.length;
    geometry.faceVertexUvs[0] = [];
    for (var _i = 0; _i < _n; _i++) {
        _ii = _i * 3;
        _a = uvIndex[_ii] * 2 - 2;
        _b = uvIndex[_ii] * 2 - 2;
        _c = uvIndex[_ii] * 2 - 2;
        geometry.faceVertexUvs[0].push([new THREE.Vector2(uv[_a], uv[_a+1]),
                        new THREE.Vector2(uv[_b], uv[_b+1]),
                        new THREE.Vector2(uv[_c], uv[_c+1])]);
    }
    geometry.uvsNeedUpdate = true;
    
    _geom.fromGeometry( geometry );
    _geom.attributes.uv.needsUpdate = true;

    geometry.dispose();
    geometry = null;
    
    updateMaterial();
}
function fillUv(_uvs, uv) {
    var _n = uv.length;
    for (var _i = 0; _i < _n; _i++) {
        _uvs[_i] = uv[_i];
    }
}
function getAndUpdateUvsJson(_uvPath) {
    // get "vt" info from _uvPath, and update uv.
    var uv = [];
    var uvIndex = [];
    var _reader = new THREE.FileLoader();
    _reader.load(_uvPath, function(_val) {
        var _data = _val.split('\n');
        var _n = _data.length;
        if (_n === 0) {
            console.log('error');
            return;
        }
        for (var _i = 0; _i < _n; _i++) {
            var _xyz = _data[_i].split(/[\s*\t]/);
            if (_xyz[0] === 'vt') {
                uv.push(parseFloat(_xyz[1]), parseFloat(_xyz[2]));
            } else if (_xyz[0] === 'f') {
                //uvIndex.push(parseInt(_xyz[1].split('/')[1]), parseInt(_xyz[2].split('/')[1]), parseInt(_xyz[3].split('/')[1]));
                uvIndex.push(parseInt(_xyz[1]), parseInt(_xyz[2]), parseInt(_xyz[3]));
            }
        }
        //console.log(uvIndex.length/3, uv.length/2);
        updateUv(uv, uvIndex);
    });
    delete _reader;
    _reader = null;
}
function getAndUpdateTex(_texPath) {
    updateTextureMapTexture(_texPath);
}
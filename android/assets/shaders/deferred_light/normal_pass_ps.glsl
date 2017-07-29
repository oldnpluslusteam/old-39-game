#ifdef GL_ES
    precision mediump float;
#endif

varying vec2 v_texCoords;
uniform sampler2D u_texture;

varying vec4 v_color;
varying vec2 v_vecX;

vec3 decodeNormal(vec4 color) {
    return 2.0 * (color.rgb - vec3(0.5));
}

vec3 encodeNormal(vec3 normal) {
    return 0.5 * (normal + vec3(1));
}

void main() {
    vec4 textureSample = texture2D(u_texture, v_texCoords);

    if (textureSample.a < 0.5) discard;

    vec3 normal = decodeNormal(textureSample);
    vec2 vecY = vec2(v_vecX.y, -v_vecX.x);
    normal = vec3(v_vecX * normal.x + vecY * normal.y, normal.z);
//    normal = normalize(normal);
    gl_FragColor = vec4(encodeNormal(normal), textureSample.a);
}

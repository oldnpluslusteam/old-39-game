#ifdef GL_ES
    precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
varying vec2 v_screenCoords;
uniform sampler2D u_texture;
uniform sampler2D u_normalTexture;

vec3 decodeNormal(vec4 color) {
    return 2.0 * (color.rgb - vec3(0.5));
}

void main() {
    vec4 lightTextureSample = texture2D(u_texture, v_texCoords);
    vec4 normalSample = texture2D(u_normalTexture, v_screenCoords);

    vec3 objectNormal = decodeNormal(normalSample) * vec3(-1,1,1);
    vec3 lightNormal = decodeNormal(lightTextureSample);
    vec3 viewDir = vec3(0,0,-1);

    float kDiff = max(0.0, dot(objectNormal, lightNormal));

    vec3 reflectDir = reflect(viewDir, objectNormal);
    float kSpec = pow(max(0.0, dot(reflectDir, lightNormal)), 10.0);

    gl_FragColor = v_color * lightTextureSample.a * (kDiff + kSpec);
}

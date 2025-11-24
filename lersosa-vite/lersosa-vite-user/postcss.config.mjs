// postcss.config.mjs
// eslint-disable-next-line import/no-anonymous-default-export
export default {
  plugins: {
    '@tailwindcss/postcss': {},   // Tailwind CSS v4+ 的 PostCSS 插件
    autoprefixer: {}               // 自动添加浏览器前缀
  }
};

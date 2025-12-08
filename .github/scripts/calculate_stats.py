#!/usr/bin/env python3
import os
import sys
import base64
import requests
from collections import defaultdict
from datetime import datetime
from github import Github
import re

def get_contributor_stats():
    """获取backend和frontend分支的PR贡献者统计"""
    token = os.getenv('GITHUB_TOKEN')
    repo_name = os.getenv('GITHUB_REPOSITORY')
    
    if not token:
        print("Error: GITHUB_TOKEN not found")
        sys.exit(1)
        
    g = Github(token)
    repo = g.get_repo(repo_name)
    
    # 统计贡献者
    contributors = defaultdict(int)
    
    # 获取backend分支的已合并PR
    try:
        backend_prs = repo.get_pulls(state='closed', base='backend', sort='created')
        for pr in backend_prs:
            if pr.merged:
                contributor = pr.user.login
                contributors[contributor] += 1
    except Exception as e:
        print(f"Warning: Could not fetch backend PRs: {e}")
    
    # 获取frontend分支的已合并PR
    try:
        frontend_prs = repo.get_pulls(state='closed', base='frontend', sort='created')
        for pr in frontend_prs:
            if pr.merged:
                contributor = pr.user.login
                contributors[contributor] += 1
    except Exception as e:
        print(f"Warning: Could not fetch frontend PRs: {e}")
    
    # 按贡献次数排序
    sorted_contributors = sorted(
        contributors.items(), 
        key=lambda x: x[1], 
        reverse=True
    )
    
    return sorted_contributors

def get_base64_image(url):
    """下载图片并转换为base64"""
    try:
        response = requests.get(url)
        if response.status_code == 200:
            return base64.b64encode(response.content).decode('utf-8')
    except Exception as e:
        print(f"Error downloading image {url}: {e}")
    return None

def generate_svg(contributors):
    """生成包含头像的SVG文件"""
    # 配置
    avatar_size = 60
    gap = 20
    columns = 10
    padding = 20
    
    # 计算尺寸
    count = len(contributors)
    rows = (count + columns - 1) // columns
    width = columns * (avatar_size + gap) + padding * 2
    height = rows * (avatar_size + gap) + padding * 2
    
    svg_content = f'''<svg width="{width}" height="{height}" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">
  <style>
    .avatar {{
      transition: all 0.3s ease;
      cursor: pointer;
      filter: drop-shadow(0 2px 4px rgba(0,0,0,0.1));
    }}
    .avatar:hover {{
      transform: scale(1.1);
      filter: drop-shadow(0 4px 8px rgba(0,0,0,0.2));
    }}
    text {{
        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Helvetica, Arial, sans-serif;
        font-size: 12px;
        fill: #666;
        text-anchor: middle;
        opacity: 0;
        transition: opacity 0.3s;
    }}
    .group:hover text {{
        opacity: 1;
    }}
  </style>
  <defs>
    <clipPath id="circle">
      <circle cx="{avatar_size/2}" cy="{avatar_size/2}" r="{avatar_size/2}" />
    </clipPath>
  </defs>
'''
    
    for i, (contributor, count) in enumerate(contributors):
        col = i % columns
        row = i // columns
        x = padding + col * (avatar_size + gap)
        y = padding + row * (avatar_size + gap)
        
        # 获取头像Base64 (使用较小尺寸以减小文件体积)
        avatar_url = f"https://github.com/{contributor}.png?size=64"
        base64_img = get_base64_image(avatar_url)
        
        if base64_img:
            img_href = f"data:image/png;base64,{base64_img}"
        else:
            img_href = avatar_url  # 降级处理
            
        profile_url = f"https://github.com/{contributor}"
        
        # 居中偏移
        cx = x + avatar_size/2
        cy = y + avatar_size/2
        
        svg_content += f'''
  <a xlink:href="{profile_url}" target="_blank">
    <g class="group" transform="translate({x}, {y})">
        <g class="avatar" transform-origin="{avatar_size/2} {avatar_size/2}">
            <image href="{img_href}" width="{avatar_size}" height="{avatar_size}" clip-path="url(#circle)" />
        </g>
        <text x="{avatar_size/2}" y="{avatar_size + 15}">{contributor}</text>
    </g>
  </a>
'''
        
    svg_content += '</svg>'
    
    # 保存文件
    os.makedirs('.github/assets', exist_ok=True)
    with open('.github/assets/contributors.svg', 'w', encoding='utf-8') as f:
        f.write(svg_content)
    print("✅ Generated contributors.svg")

def update_readme(contributors):
    """更新README.md文件"""
    target_file = os.getenv('TARGET_FILE', 'README.md')
    
    # 首先生成SVG
    generate_svg(contributors)
    
    # 读取现有内容
    if os.path.exists(target_file):
        with open(target_file, 'r', encoding='utf-8') as f:
            content = f.read()
    else:
        content = ""
    
    # 头部样式
    stats_section = f"""
<br>

## <img src="https://api.iconify.design/mdi:account-group.svg?color=%23000000" width="24" height="24" valign="bottom"> 贡献者风采

感谢每一位参与 **Rookie Blog** 开发的贡献者，是你们让这个项目变得更好。

<div align="center">
  <img src=".github/assets/contributors.svg" alt="Contributors" width="100%">
</div>
"""
    
    # 使用标记来替换内容
    start_marker = "<!-- CONTRIBUTOR_STATS_START -->"
    end_marker = "<!-- CONTRIBUTOR_STATS_END -->"
    
    if start_marker in content and end_marker in content:
        # 替换现有内容
        pattern = f"{start_marker}.*?{end_marker}"
        new_section = f"{start_marker}\n{stats_section}\n{end_marker}"
        content = re.sub(pattern, new_section, content, flags=re.DOTALL)
    else:
        # 在文件末尾添加新内容
        content += f"\n\n{start_marker}\n{stats_section}\n{end_marker}"
    
    # 写回文件
    with open(target_file, 'w', encoding='utf-8') as f:
        f.write(content)
    
    print(f"✅ 已更新 {target_file} 文件")

def main():
    try:
        print("开始统计贡献者数据...")
        contributors = get_contributor_stats()
        
        if not contributors:
            print("⚠️ 没有找到贡献者数据")
            return
        
        print(f"找到 {len(contributors)} 位贡献者")
        for i, (contributor, count) in enumerate(contributors[:10], 1):
            print(f"{i}. @{contributor}: {count} 个合并PR")
        
        update_readme(contributors)
        print("✅ 统计完成！")
        
    except Exception as e:
        print(f"❌ 发生错误: {str(e)}")
        sys.exit(1)

if __name__ == "__main__":
    main()

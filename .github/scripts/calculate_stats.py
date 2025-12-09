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

def generate_contributor_html(contributors):
    """生成贡献者HTML列表 (流式布局 - 左对齐)"""
    
    html_content = """
<div align="left">
<br/>
<p style="text-align: left;">
"""
    
    for contributor, count in contributors:
        # 图片处理：圆形裁切 + 高清
        avatar_url = f"https://wsrv.nl/?url=github.com/{contributor}.png&h=120&w=120&fit=cover&mask=circle&q=80"
        profile_url = f"https://github.com/{contributor}"
        
        # 纯流式布局
        # margin-right: 15px 确保右侧间距，第一行左对齐
        # vertical-align: middle 消除图片底部幽灵空白（小黑线）
        html_content += f"""<a href="{profile_url}" title="{contributor} ({count} contributions)">
    <img src="{avatar_url}" width="60" height="60" alt="{contributor}" style="margin-right: 15px; margin-bottom: 15px; display: inline-block; vertical-align: middle;" />
</a>"""
            
    html_content += "\n</p>\n<br/>\n</div>"
    return html_content

def update_readme(contributors):
    """更新README.md文件"""
    target_file = os.getenv('TARGET_FILE', 'README.md')
    
    # 读取现有内容
    if os.path.exists(target_file):
        with open(target_file, 'r', encoding='utf-8') as f:
            content = f.read()
    else:
        content = ""
    
    # 生成HTML内容
    contributors_html = generate_contributor_html(contributors)
    
    # 头部样式
    stats_section = f"""
<br>

## <img src="https://api.iconify.design/mdi:account-group-outline.svg?color=%23000000" width="24" height="24" style="vertical-align: -5px;"> 贡献者风采

感谢每一位参与 **Rookie Blog** 开发的贡献者，是你们让这个项目变得更好。

{contributors_html}
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
    
    print(f"Updated {target_file}")

def main():
    try:
        print("Starting contributor statistics...")
        contributors = get_contributor_stats()
        
        if not contributors:
            print("No contributors found")
            return
        
        print(f"Found {len(contributors)} contributors")
        for i, (contributor, count) in enumerate(contributors[:10], 1):
            print(f"{i}. {contributor}: {count} merged PRs")
        
        update_readme(contributors)
        print("Statistics completed!")
        
    except Exception as e:
        print(f"Error: {str(e)}")
        sys.exit(1)

if __name__ == "__main__":
    main()

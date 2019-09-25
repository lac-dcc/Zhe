#!/usr/bin/env python

from setuptools import setup, find_packages

setup(
    name='parsy',
    version='0.0.1',
    description='ParSY Synthesize parser from examples',
    author='João Saffran',
    author_email='joaosaffran@gmail.com',
    url='https://joaosaffran.cc',
    license="",
    packages=find_packages(),
    include_package_data=True,
    install_requires=[],
    dependency_links=[],
    test_suite='nose.collector',
)
